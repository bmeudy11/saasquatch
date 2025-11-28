import React, { useState, useEffect, useCallback } from 'react';
import { Helmet } from 'react-helmet';
import RouteForm from '../../components/RouteForm/RouteForm';
import {
    findAmenitiesByCurrentLocation,
    findAmenitiesByTypes, findNearbyAmenity,
    generateRandomDestination,
    generateRoute,
    getServerHealth,
    getAIPOIs
} from '../../components/API/API';
import { v4 as uuidv4 } from 'uuid';
import './RoutePlannerPage.scss';
import NearestAmenityForm from "../../components/NearestAmenityForm/NearestAmenityForm";
import MapDisplay from "../../components/Map Display/MapDisplay";

export function RoutePlannerPage(props) {
    const { sideBarOpen, alert } = props;
    const [loadingRoute, setLoadingRoute] = useState(false);
    const [loadingAmenity, setLoadingAmenity] = useState(false);
    const [generatedRoute, setGeneratedRoute] = useState();
    const [routeAddresses, setRouteAddresses] = useState(); // Store original addresses
    const [amenities, setAmenities] = useState();
    const [health, setHealth] = useState();
    const [healthError, setHealthError] = useState();

    const getHealth = useCallback(async () => {
        const [success, result] = await getServerHealth();
        if (!success) {
            const msgPayload =  {
                id: uuidv4(),
                type: 'error',
                message: `Failed to get health status: ${result}`,
            };
            setHealthError(true);
            alert(msgPayload);
        } else {
            setHealthError(false);
            setHealth(result?.health.status);
        }
    }, [alert]);

    useEffect(() => {
        if (!healthError && !health) {
            getHealth().then();
        }
    }, [getHealth, health, healthError]);

    const submitHandler = async (data) => {
        setLoadingRoute(true);

        let success, result;

        // Check which destination type was selected
        if (data.destinationType === 'random') {
            // Random destination - waypoints not supported
            if (data.waypoints && data.waypoints.length > 0) {
                const msgPayload = {
                    id: uuidv4(),
                    type: 'warn',
                    message: 'Waypoints are not supported for random destination routes.',
                };
                alert(msgPayload);
                setLoadingRoute(false);
                return;
            }
            const payload = {
                origin: data.originAddress,
                radius: data.radius,
            };
            [success, result] = await generateRandomDestination(payload);
        } else {
            // Manual destination with optional waypoints
            const payload = {
                origin: data.originAddress,
                destination: data.destinationAddress,
                waypoints: data.waypoints || []
            };
            [success, result] = await generateRoute(payload);
        }

        if (success) {
            setGeneratedRoute(result);
            // Store original addresses for AI POI search
            if (data.destinationType === 'manual') {
                setRouteAddresses({
                    origin: data.originAddress,
                    destination: data.destinationAddress
                });
            } else {
                // For random destination, store what we have
                setRouteAddresses({
                    origin: data.originAddress,
                    destination: result.destination || result.routeDetails?.destination
                });
            }
            const msgPayload =  {
                id: uuidv4(),
                type: 'success',
                message: 'Route generated successfully!',
            };
            alert(msgPayload);
        } else {
            const msgPayload =  {
                id: uuidv4(),
                type: 'error',
                message: `Failed to get location: ${result}`,
            };
            alert(msgPayload);
        }
        setLoadingRoute(false);
    };

    const submitAmenityHandler = async (originType, singleType, data) => {
        setLoadingAmenity(true);

        let success, result;

        // Check if AI mode is selected
        if (originType === 'ai') {
            // AI-powered POI suggestions
            console.log('AI POI Request Data:', JSON.stringify(data, null, 2));
            [success, result] = await getAIPOIs(data);
            console.log('AI POI Response Success:', success);
            console.log('AI POI Response Result:', JSON.stringify(result, null, 2));
        } else if (originType !== 'manual') {
            // Amenities based on current location
            [success, result] = await findAmenitiesByCurrentLocation(data);
        } else {
            // Standard manual location mode
            if (singleType) {
                [success, result] = await findNearbyAmenity(data);
            } else {
                [success, result] = await findAmenitiesByTypes(data);
            }
        }

        if (success) {
            setAmenities(result);
            const msgPayload =  {
                id: uuidv4(),
                type: 'success',
                message: originType === 'ai' ? 'AI POI suggestions found successfully!' : 'Amenities found successfully!',
            };
            alert(msgPayload);
        } else {
            const msgPayload =  {
                id: uuidv4(),
                type: 'error',
                message: `Failed to find amenities: ${result}`,
            };
            alert(msgPayload);
        }
        setLoadingAmenity(false);
    };

    return (
        <section id="route-planner-section" style={{width: sideBarOpen ? '85%' : '97%'}}>
            <Helmet>
                <title>SaaSquatch | Plan A Route</title>
            </Helmet>
            <div id="route-planner-wrapper-div" className="route-planner-wrapper-div">
                <div id="route-planner-content" className="route-planner-content">
                    <div id="health-div">
                        <div className={`status${health === 'UP' ? ' online' : ' offline'}`}>
                            SERVER {health === 'UP' ? 'ONLINE' : 'OFFLINE'}
                        </div>
                    </div>
                    <div id="destination-endpoints-div">
                        <div className="endpoints-div">
                            <h1>Route Planner</h1>

                            {/* Forms Container - Side by Side */}
                            <div className="forms-container">
                                <div className="form-column">
                                    <h2>Route Planning</h2>
                                    <RouteForm
                                        alert={alert}
                                        onSubmit={submitHandler}
                                        loading={loadingRoute}
                                        updateGeneratedRoute={setGeneratedRoute}
                                    />
                                </div>

                                <div className="form-column">
                                    <h2>Find Amenities</h2>
                                    <NearestAmenityForm
                                        alert={alert}
                                        onSubmit={submitAmenityHandler}
                                        loading={loadingAmenity}
                                        updateAmenity={setAmenities}
                                        routeAddresses={routeAddresses}
                                    />

                                    {amenities && (
                                        <>
                                            <h3>Amenities Found:</h3>
                                            <pre>{JSON.stringify(amenities, null, 4)}</pre>
                                        </>
                                    )}
                                </div>
                            </div>

                            {/* Results Section - Full Width Below */}
                            {generatedRoute && (
                                <div className="results-section">
                                    <h3>Generated Route Data:</h3>
                                    <pre>{JSON.stringify(generatedRoute, null, 4)}</pre>

                                    <h3>Route Map:</h3>
                                    <MapDisplay routeData={generatedRoute} />
                                </div>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </section>
    );
}
