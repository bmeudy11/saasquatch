import React, { useState } from 'react';
import { getCurrentLocation } from '../API/API';
import { v4 as uuidv4 } from 'uuid';

/**
 * RouteForm Component
 * Handles the form UI and state for route planning
 * Passes form data up to parent component via onSubmit callback
 */

 //state vars
export default function RouteForm(props) {
    const { onSubmit, loading, alert, updateGeneratedRoute } = props;
    // State for origin
    const [originType, setOriginType] = useState('manual'); // 'manual' or 'location'
    const [originAddress, setOriginAddress] = useState('');
    const [gettingLocation, setGettingLocation] = useState(false);

    // State for destination
    const [destinationType, setDestinationType] = useState('manual'); // 'manual' or 'random'
    const [destinationAddress, setDestinationAddress] = useState('');
    const [radius, setRadius] = useState(50000); // Default 50,000 feet (~9.5 miles)

    // State for waypoints
    const [waypoints, setWaypoints] = useState([]);
    const [waypointInput, setWaypointInput] = useState('');

    // Add waypoint to list
    const handleAddWaypoint = () => {
        if (!waypointInput.trim()) {
            const msgPayload = {
                id: uuidv4(),
                type: 'warn',
                message: 'Please enter a waypoint address',
            };
            alert(msgPayload);
            return;
        }

        setWaypoints(prev => [...prev, waypointInput.trim()]);
        setWaypointInput('');
    };

    // Remove waypoint from list
    const handleRemoveWaypoint = (index) => {
        setWaypoints(prev => prev.filter((_, i) => i !== index));
    };

    // Get user's current location using geolocation endpoint
    const getCurrentLocationHandler = async () => {
        setGettingLocation(true);

        const [success, result] = await getCurrentLocation();
        //success/failure handing
        if (success) {
            // API returns: { latitude, longitude, accessPointsUsed }
            const { latitude, longitude } = result;
            setOriginAddress(`${latitude},${longitude}`);
            setGettingLocation(false);
            const msgPayload =  {
                id: uuidv4(),
                type: 'success',
                message: 'Location acquired successfully!',
            };
            alert(msgPayload);
        } else {
            setGettingLocation(false);
            const msgPayload =  {
                id: uuidv4(),
                type: 'error',
                message: `Failed to get location: ${result}`,
            };
            alert(msgPayload);
        }
    };

    // Handle form submission
    const handleSubmit = (e) => {
        e.preventDefault();

        let msgPayload =  {
            id: uuidv4(),
            type: 'error',
        };

        // Validate inputs
        if (!originAddress && originType === 'manual') {
            msgPayload = {
                ...msgPayload,
                message: 'Please enter an origin address',
            }
            alert(msgPayload);
            return;
        }

        if (!originAddress && originType === 'location') {
            msgPayload = {
                ...msgPayload,
                message: 'Please get your current location first',
            }
            alert(msgPayload);
            return;
        }

        if (!destinationAddress && destinationType === 'manual') {
            msgPayload = {
                ...msgPayload,
                message: 'Please enter a destination address',
            }
            alert(msgPayload);
            return;
        }

        // Build form data object
        const formData = {
            originType,
            originAddress,
            destinationType,
            destinationAddress,
            radius,
            waypoints,
        };

        // Pass data to parent component
        onSubmit(formData);
    };

    // Handle clear
    const handleClear = () => {
        setOriginAddress('');
        setDestinationAddress('');
        setOriginType('manual');
        setDestinationType('manual');
        setRadius(50000);
        setWaypoints([]);
        setWaypointInput('');
        updateGeneratedRoute(undefined);
    };

    return (
        <form onSubmit={handleSubmit} className="route-form">
            {/* ORIGIN SECTION */}
            <div className="form-section">
                <h2>Origin</h2>

                <div className="toggle-group">
                    <label>
                        <input
                            type="radio"
                            name="originType"
                            value="manual"
                            checked={originType === 'manual'}
                            onChange={(e) => setOriginType(e.target.value)}
                        />
                        Enter Address
                    </label>
                    <label>
                        <input
                            type="radio"
                            name="originType"
                            value="location"
                            checked={originType === 'location'}
                            onChange={(e) => setOriginType(e.target.value)}
                        />
                        Use My Location
                    </label>
                </div>

                {originType === 'manual' ? (
                    <input
                        type="text"
                        placeholder="Enter origin address (e.g., Charleston, SC)"
                        value={originAddress}
                        onChange={(e) => setOriginAddress(e.target.value)}
                        className="address-input"
                    />
                ) : (
                    <div className="location-section">
                        <button
                            type="button"
                            onClick={getCurrentLocationHandler}
                            disabled={gettingLocation}
                            className="main-button"
                        >
                            {gettingLocation ? 'Getting Location...' : 'Get Current Location'}
                        </button>
                        {originAddress && (
                            <p className="location-display">
                                Location: {originAddress}
                            </p>
                        )}
                    </div>
                )}
            </div>

            {/* DESTINATION SECTION */}
            <div className="form-section">
                <h2>Destination</h2>

                <div className="toggle-group">
                    <label>
                        <input
                            type="radio"
                            name="destinationType"
                            value="manual"
                            checked={destinationType === 'manual'}
                            onChange={(e) => setDestinationType(e.target.value)}
                        />
                        Enter Address
                    </label>
                    <label>
                        <input
                            type="radio"
                            name="destinationType"
                            value="random"
                            checked={destinationType === 'random'}
                            onChange={(e) => setDestinationType(e.target.value)}
                        />
                        Find Within a Radius
                    </label>
                </div>

                {destinationType === 'manual' ? (
                    <input
                        type="text"
                        placeholder="Enter destination address (e.g., Atlanta, GA)"
                        value={destinationAddress}
                        onChange={(e) => setDestinationAddress(e.target.value)}
                        className="address-input"
                    />
                ) : (
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
                        <p className="radius-help">
                            {(radius / 5280).toFixed(2)} miles
                        </p>
                    </div>
                )}
            </div>

            {/* WAYPOINTS SECTION */}
            <div className="form-section">
                <h2>Waypoints (Optional)</h2>
                {destinationType === 'manual' ? (
                    <>
                        <p style={{margin: '0 0 1em 0', fontSize: '0.9em'}}>Add stops along your route</p>

                    <div style={{display: 'flex', gap: '0.5em', marginBottom: '1em'}}>
                        <input
                            type="text"
                            placeholder="Enter waypoint address"
                            value={waypointInput}
                            onChange={(e) => setWaypointInput(e.target.value)}
                            onKeyPress={(e) => e.key === 'Enter' && (e.preventDefault(), handleAddWaypoint())}
                            className="address-input"
                            style={{flex: 1}}
                        />
                        <button
                            type="button"
                            onClick={handleAddWaypoint}
                            className="main-button"
                        >
                            Add
                        </button>
                    </div>

                    {waypoints.length > 0 && (
                        <div style={{marginTop: '1.5em', padding: '1em', backgroundColor: '#E3E3E3', borderRadius: '8px'}}>
                            <h3 style={{margin: '0 0 0.75em 0', fontSize: '1em', color: '#111111'}}>Current Waypoints ({waypoints.length}):</h3>
                            {waypoints.map((waypoint, index) => (
                                <div key={index} style={{display: 'flex', alignItems: 'center', gap: '0.75em', padding: '0.75em', marginBottom: '0.5em', backgroundColor: 'white', border: '1px solid #C1C1C1', borderRadius: '6px'}}>
                                    <span style={{display: 'flex', alignItems: 'center', justifyContent: 'center', minWidth: '24px', height: '24px', backgroundColor: '#154734', color: '#F8F9F8', borderRadius: '50%', fontSize: '0.85em', fontWeight: 'bold'}}>{index + 1}</span>
                                    <span style={{flex: 1, fontSize: '0.95em', overflow: 'hidden', textOverflow: 'ellipsis', whiteSpace: 'nowrap', color: '#111111'}}>{waypoint}</span>
                                    <button
                                        type="button"
                                        onClick={() => handleRemoveWaypoint(index)}
                                        style={{width: '24px', height: '24px', backgroundColor: '#DB3131', color: '#F8F9F8', border: 'none', borderRadius: '50%', fontSize: '1.4em', lineHeight: '1', cursor: 'pointer'}}
                                        title="Remove waypoint"
                                    >
                                        ×
                                    </button>
                                </div>
                            ))}
                        </div>
                    )}
                    </>
                ) : (
                    <div style={{
                        padding: '1em',
                        backgroundColor: '#FFF3CD',
                        border: '1px solid #FFE69C',
                        borderRadius: '8px',
                        color: '#856404'
                    }}>
                        <p style={{margin: 0, fontSize: '0.95em'}}>
                            <strong>ℹ️ Note:</strong> Waypoints are not supported for radius-based random destination routes.
                            To use waypoints, select "Enter Address" for your destination.
                        </p>
                    </div>
                )}
            </div>

            {/* BUTTONS */}
            <div className="button-group">
                <button
                    type="submit"
                    disabled={loading || gettingLocation}
                    className="main-button"
                >
                    {loading ? 'Generating Route...' : 'Generate Route'}
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
