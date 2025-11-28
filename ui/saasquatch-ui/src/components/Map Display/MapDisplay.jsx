import React, { useState, useEffect } from 'react';
import { GoogleMap, Polyline, Marker, useJsApiLoader } from '@react-google-maps/api';

// Define libraries array as constant to prevent reloading
const libraries = ['geometry'];

/**
 * MapDisplay Component
 * Displays a Google Map with route directions
 *
 * @param {Object} routeData - Route data from backend (contains encodedPolyline, origin, destination)
 */
function MapDisplay({ routeData }) {
    const { isLoaded, loadError } = useJsApiLoader({
        googleMapsApiKey: process.env.REACT_APP_GOOGLE_MAPS_API_KEY || "",
        libraries: libraries, // Required for polyline decoding
    });

    const [decodedPath, setDecodedPath] = useState(null);
    const [mapCenter, setMapCenter] = useState({ lat: 32.7765, lng: -79.9311 }); // Default: Charleston, SC
    const [waypointPositions, setWaypointPositions] = useState([]);

    const mapContainerStyle = {
        width: '100%',
        maxWidth: '1200px',
        height: '500px',
        margin: '20px auto',
        borderRadius: '8px',
        boxShadow: '0 2px 8px rgba(0,0,0,0.2)'
    };

    // Decode the polyline when routeData changes
    useEffect(() => {
        console.log('Route data received:', routeData);

        // Handle both response formats:
        // 1. Direct RouteResponse: { encodedPolyline, origin, destination, ... }
        // 2. Wrapped response: { routeDetails: { encodedPolyline, ... } }
        const polyline = routeData?.encodedPolyline || routeData?.routeDetails?.encodedPolyline;

        console.log('Has encodedPolyline?', polyline);
        console.log('Google loaded?', !!window.google);
        console.log('Geometry loaded?', !!window.google?.maps?.geometry);

        // Check if Google Maps AND the geometry library are loaded
        if (routeData && polyline && window.google && window.google.maps && window.google.maps.geometry) {
            try {
                console.log('Attempting to decode polyline...');
                // Use Google's built-in polyline decoder
                const path = window.google.maps.geometry.encoding.decodePath(polyline);
                console.log('Decoded path:', path);
                console.log('Path length:', path?.length);
                setDecodedPath(path);

                // Set map center to the middle of the route
                if (path && path.length > 0) {
                    const midpoint = path[Math.floor(path.length / 2)];
                    setMapCenter({ lat: midpoint.lat(), lng: midpoint.lng() });
                    console.log('Map centered at:', midpoint.lat(), midpoint.lng());
                }
            } catch (error) {
                console.error('Error decoding polyline:', error);
            }
        } else {
            console.log('Missing data for polyline rendering');
        }
    }, [routeData, isLoaded]); // Re-run when routeData or Google Maps loads

    // Geocode waypoint addresses to get their coordinates
    useEffect(() => {
        if (!window.google || !window.google.maps || !routeData?.waypoints) {
            setWaypointPositions([]);
            return;
        }

        const geocoder = new window.google.maps.Geocoder();
        const waypoints = routeData.waypoints || [];

        // Geocode all waypoint addresses
        Promise.all(
            waypoints.map(address =>
                new Promise((resolve) => {
                    geocoder.geocode({ address: address }, (results, status) => {
                        if (status === 'OK' && results[0]) {
                            resolve({
                                lat: results[0].geometry.location.lat(),
                                lng: results[0].geometry.location.lng(),
                                address: address
                            });
                        } else {
                            console.error('Geocoding failed for:', address, status);
                            resolve(null);
                        }
                    });
                })
            )
        ).then(positions => {
            setWaypointPositions(positions.filter(p => p !== null));
        });
    }, [routeData?.waypoints, isLoaded]);

    if (loadError) {
        return <div className="map-error">Error loading maps</div>;
    }

    if (!isLoaded) {
        return <div className="map-loading">Loading map...</div>;
    }

    // If no route data, show empty map
    const polyline = routeData?.encodedPolyline || routeData?.routeDetails?.encodedPolyline;
    if (!routeData || !polyline) {
        return (
            <GoogleMap
                mapContainerStyle={mapContainerStyle}
                center={mapCenter}
                zoom={10}
            >
                {/* Empty map */}
            </GoogleMap>
        );
    }

    return (
        <GoogleMap
            mapContainerStyle={mapContainerStyle}
            center={mapCenter}
            zoom={10}
        >
            {/* Draw the route as a blue polyline */}
            {decodedPath && (
                <Polyline
                    path={decodedPath}
                    options={{
                        strokeColor: '#2196F3',
                        strokeOpacity: 0.8,
                        strokeWeight: 5,
                    }}
                />
            )}

            {/* Add markers for start and end points */}
            {decodedPath && decodedPath.length > 0 && (
                <>
                    <Marker
                        position={decodedPath[0]}
                        label="A"
                        title={routeData.origin || routeData.routeDetails?.origin}
                    />

                    {/* Add markers for waypoints */}
                    {waypointPositions.map((waypoint, index) => {
                        // Label waypoints as B, C, D, etc.
                        const label = String.fromCharCode(66 + index); // B is char code 66

                        return (
                            <Marker
                                key={index}
                                position={{ lat: waypoint.lat, lng: waypoint.lng }}
                                label={label}
                                title={waypoint.address}
                            />
                        );
                    })}

                    <Marker
                        position={decodedPath[decodedPath.length - 1]}
                        label={String.fromCharCode(66 + waypointPositions.length)}
                        title={routeData.destination || routeData.routeDetails?.destination}
                    />
                </>
            )}
        </GoogleMap>
    );
}

export default MapDisplay;
