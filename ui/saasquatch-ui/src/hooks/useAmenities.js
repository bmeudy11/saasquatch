import { useState, useCallback } from 'react';
import { findNearbyAmenity, findAmenitiesByTypes } from '../components/API/API';

/* Hook for searching amenities near a location
 *
 * @example
 * const { amenities, loading, error, searchAmenities, searchMultipleTypes } = useAmenities();
 *
 * // Search for a single type
 * await searchAmenities({
 *   latitude: 32.7765,
 *   longitude: -79.9311,
 *   radius: 1500,
 *   type: 'restaurant'
 * });
 *
 * // Search for multiple types
 * await searchMultipleTypes({
 *   latitude: 32.7765,
 *   longitude: -79.9311,
 *   radius: 2000,
 *   types: ['restaurant', 'gas_station', 'cafe']
 * });
 */

 //hook/state declarations
export const useAmenities = () => {
    const [amenities, setAmenities] = useState([]);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    //function to search for one amenity type
    const searchAmenities = useCallback(async (amenityData) => {
        setLoading(true);
        setError(null);

        //api call
        const [success, result] = await findNearbyAmenity(amenityData);

        if (success) {
            setAmenities(result.amenities || []);
            setError(null);
        } else {
            setError(result);
            setAmenities([]);
        }

        setLoading(false);
        return [success, result];
    }, []);
    //function to search for multiple amenity types
    const searchMultipleTypes = useCallback(async (amenityData) => {
        setLoading(true);
        setError(null);

        const [success, result] = await findAmenitiesByTypes(amenityData);

        if (success) {
            setAmenities(result.amenities || []);
            setError(null);
        } else {
            setError(result);
            setAmenities([]);
        }

        setLoading(false);
        return [success, result];
    }, []);
    //clear function
    const clearAmenities = useCallback(() => {
        setAmenities([]);
        setError(null);
    }, []);
    //retunr data object to component
    return {
        amenities,
        loading,
        error,
        searchAmenities,
        searchMultipleTypes,
        clearAmenities,
    };
};

export default useAmenities;
