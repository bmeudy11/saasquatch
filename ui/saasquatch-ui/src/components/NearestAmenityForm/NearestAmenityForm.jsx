import React, { useState, useEffect } from 'react';
import { v4 as uuidv4 } from 'uuid';
import  { PlaceTypes } from '../../utils/PlaceTypes';

/**
 * NearestAmenityForm Component
 * Handles the form UI and state for finding the nearest amenity
 * Passes form data up to parent component via onSubmit callback
 */

//state vars
export default function NearestAmenityForm(props) {
    const { onSubmit, loading, alert, updateAmenity, routeAddresses } = props;

    // State for amenity mode
    const [amenityMode, setAmenityMode] = useState('standard'); // 'standard' or 'ai'

    // State for standard amenity mode
    const [originType, setOriginType] = useState('manual');
    const [latitude, setLatitude] = useState('');
    const [longitude, setLongitude] = useState('');
    const [types, setTypes] = useState([]);
    const [radius, setRadius] = useState(50000); // Default 50,000 feet (~9.5 miles)

    // State for AI mode
    const [aiOrigin, setAiOrigin] = useState('');
    const [aiDestination, setAiDestination] = useState('');
    const [aiQuery, setAiQuery] = useState('');

    // Auto-populate AI origin/destination from route addresses
    useEffect(() => {
        if (amenityMode === 'ai' && routeAddresses) {
            // Always update with the latest route addresses
            setAiOrigin(routeAddresses.origin || '');
            setAiDestination(routeAddresses.destination || '');
        }
    }, [amenityMode, routeAddresses]);

    // Handle form submission
    const handleSubmit = (e) => {
        e.preventDefault();

        let formData;
        let msgPayload =  {
            id: uuidv4(),
            type: 'error',
        };

        // AI Mode validation and submission
        if (amenityMode === 'ai') {
            if (!aiOrigin) {
                msgPayload = {
                    ...msgPayload,
                    message: 'Please enter an origin address',
                }
                alert(msgPayload);
                return;
            }

            if (!aiDestination) {
                msgPayload = {
                    ...msgPayload,
                    message: 'Please enter a destination address',
                }
                alert(msgPayload);
                return;
            }

            if (!aiQuery.trim()) {
                msgPayload = {
                    ...msgPayload,
                    message: 'Please enter a query (e.g., "coffee shops", "scenic viewpoints")',
                }
                alert(msgPayload);
                return;
            }

            formData = {
                origin: aiOrigin,
                destination: aiDestination,
                query: aiQuery
            };

            // Pass 'ai' as the mode to parent component
            onSubmit('ai', null, formData);
            return;
        }

        // Standard Mode validation (existing logic)
        if (originType === 'manual' && !latitude) {
            msgPayload = {
                ...msgPayload,
                message: 'Please enter your latitude!',
            }
            alert(msgPayload);
            return;
        }

        if (originType === 'manual' && !longitude) {
            msgPayload = {
                ...msgPayload,
                message: 'Please enter your longitude!',
            }
            alert(msgPayload);
            return;
        }

        if (!types || types?.length === 0) {
            msgPayload = {
                ...msgPayload,
                message: 'Please select an amenity type',
            }
            alert(msgPayload);
            return;
        }

        formData = {
            latitude,
            longitude,
            radius,
        };

        const singleType = types.length === 1;
        if (singleType) {
            if (originType !== 'manual') {
                formData = {
                    radius,
                    type: types[0],
                }
            } else {
                formData = {
                    ...formData,
                    type: types[0],
                }
            }
        } else {
            formData = {
                ...formData,
                types,
            }
        }

        // Pass data to parent component
        onSubmit(originType, singleType, formData);
    };

    // Handle clear
    const handleClear = () => {
        setAmenityMode('standard');
        setOriginType('manual');
        setLatitude('');
        setLongitude('');
        setTypes([]);
        setRadius(50000);
        setAiOrigin('');
        setAiDestination('');
        setAiQuery('');
        updateAmenity(undefined);
    };

    return (
        <form onSubmit={handleSubmit} className="route-form">
            <div className="form-section">
                <h2>Find Amenities</h2>

                {/* AMENITY MODE TOGGLE */}
                <div className="toggle-group" style={{marginBottom: '1.5em'}}>
                    <label>
                        <input
                            type="radio"
                            name="amenityMode"
                            value="standard"
                            checked={amenityMode === 'standard'}
                            onChange={(e) => setAmenityMode(e.target.value)}
                        />
                        Standard Amenity
                    </label>
                    <label>
                        <input
                            type="radio"
                            name="amenityMode"
                            value="ai"
                            checked={amenityMode === 'ai'}
                            onChange={(e) => setAmenityMode(e.target.value)}
                        />
                        Custom Amenity (AI)
                    </label>
                </div>

                {/* STANDARD AMENITY MODE */}
                {amenityMode === 'standard' && (
                    <>
                        <div className="toggle-group">
                            <label>
                                <input
                                    type="radio"
                                    name="originType"
                                    value="manual"
                                    checked={originType === 'manual'}
                                    onChange={(e) => setOriginType(e.target.value)}
                                />
                                Manual Location
                            </label>
                            <label>
                                <input
                                    type="radio"
                                    name="originType"
                                    value="location"
                                    checked={originType === 'location'}
                                    onChange={(e) => setOriginType(e.target.value)}
                                />
                                Use My Location (Built-In)
                            </label>
                        </div>

                        {originType === 'manual' ? (
                            <div>
                                <input
                                    id="lat"
                                    type="text"
                                    placeholder="Enter Latitude"
                                    value={latitude}
                                    onChange={(e) => setLatitude(e.target.value.trim())}
                                    className="address-input"
                                />
                                <input
                                    id="long"
                                    type="text"
                                    placeholder="Enter Longitude"
                                    value={longitude}
                                    onChange={(e) => setLongitude(e.target.value.trim())}
                                    className="address-input"
                                />
                            </div>
                        ) : (<></>)}

                        <div className="types-div">
                            <label>Amenity Type(s):</label>
                            <select
                                multiple
                                value={types}
                                onChange={(e) => {
                                    const selected = Array.from(e.target.selectedOptions).map(o => o.value);
                                    setTypes(selected);
                                }}
                            >
                                {Object.values(PlaceTypes).map(type => (
                                    <option key={type} value={type}>
                                        {type.replace(/_/g, ' ')}
                                    </option>
                                ))}
                            </select>
                        </div>

                        <div className="radius-section">
                            <label htmlFor="radius">Search Radius (feet):</label>
                            <input
                                type="number"
                                id="radius"
                                value={radius}
                                onChange={(e) => setRadius(parseInt(e.target.value))}
                                min="1000"
                                max="500000"
                                step="1000"
                                className="radius-input"
                            />
                        </div>
                    </>
                )}

                {/* AI CUSTOM AMENITY MODE */}
                {amenityMode === 'ai' && (
                    <>
                        <p style={{margin: '0 0 1em 0', fontSize: '0.9em', color: '#555'}}>
                            Use natural language to find POIs along your route
                        </p>

                        {routeAddresses && (
                            <div style={{
                                padding: '0.75em',
                                backgroundColor: '#E8F5E9',
                                borderRadius: '4px',
                                marginBottom: '1em',
                                fontSize: '0.85em',
                                color: '#2E7D32'
                            }}>
                                <strong>✓ Route detected:</strong> Origin and destination auto-filled from your generated route
                            </div>
                        )}

                        <input
                            type="text"
                            placeholder="Origin address (e.g., Charleston, SC)"
                            value={aiOrigin}
                            onChange={(e) => setAiOrigin(e.target.value)}
                            className="address-input"
                        />

                        <input
                            type="text"
                            placeholder="Destination address (e.g., Atlanta, GA)"
                            value={aiDestination}
                            onChange={(e) => setAiDestination(e.target.value)}
                            className="address-input"
                        />

                        <input
                            type="text"
                            placeholder="What are you looking for? (e.g., coffee shops, scenic viewpoints)"
                            value={aiQuery}
                            onChange={(e) => setAiQuery(e.target.value)}
                            className="address-input"
                        />
                    </>
                )}
            </div>

            {/* BUTTONS */}
            <div className="button-group">
                <button
                    type="submit"
                    disabled={loading}
                    className="main-button"
                >
                    {loading ? 'Finding...' : 'Find Amenities'}
                </button>
                <button
                    type="button"
                    onClick={handleClear}
                    className="main-button"
                >
                    Clear
                </button>
            </div>
        </form>
    );
}
