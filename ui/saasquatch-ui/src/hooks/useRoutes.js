import { useState, useCallback } from 'react';
import { generateRoute } from '../components/API/API';

/**
 * Hook for generating routes between locations
 *
 * @example
 * const { route, loading, error, createRoute } = useRoutes();
 *
 * const handleGenerateRoute = async () => {
 *   await createRoute({
 *     origin: 'Charleston, SC',
 *     destination: 'Atlanta, GA',
 *     waypoints: ['Columbia, SC']
 *   });
 * };
 */

//hook/state declaration
export const useRoutes = () => {
    const [route, setRoute] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);
    //route function
    const createRoute = useCallback(async (routeData) => {
        setLoading(true);
        setError(null);
        //call to API
        const [success, result] = await generateRoute(routeData);

        if (success) {
            setRoute(result);
            setError(null);
        } else {
            setError(result);
            setRoute(null);
        }

        setLoading(false);
        return [success, result];
    }, []);
    //clear route function
    const clearRoute = useCallback(() => {
        setRoute(null);
        setError(null);
    }, []);
    //return data object to component
    return {
        route,
        loading,
        error,
        createRoute,
        clearRoute,
    };
};

export default useRoutes;
