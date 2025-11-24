import React, { useState } from 'react';
import { v4 as uuidv4 } from 'uuid';
import  { PlaceTypes } from '../../utils/PlaceTypes';

/**
 * NearestAmenityForm Component
 * Handles the form UI and state for finding the nearest amenity
 * Passes form data up to parent component via onSubmit callback
 */

//state vars
export default function NearestAmenityForm(props) {
    const { onSubmit, loading, alert, updateAmenity } = props;
    // State for origin
    const [originType, setOriginType] = useState('manual');
    const [latitude, setLatitude] = useState('');
    const [longitude, setLongitude] = useState('');
    const [types, setTypes] = useState([]);

    // State for destination
    const [radius, setRadius] = useState(50000); // Default 50,000 feet (~9.5 miles)

    // Handle form submission
    const handleSubmit = (e) => {
        e.preventDefault();

        let formData;
        let msgPayload =  {
            id: uuidv4(),
            type: 'error',
        };

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
        setOriginType('manual');
        setLatitude('');
        setLongitude('');
        setTypes([]);
        setRadius(50000);
        updateAmenity(undefined);
    };

    return (
        <form onSubmit={handleSubmit} className="route-form">
            <div className="form-section">
                <h2>Find Amenities</h2>

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
