import { useState, useCallback } from 'react';

/**
 * Generic hook for making API calls with loading and error states
 *
 * @example
 * const { data, loading, error, execute } = useApi(generateRoute);
 *
 * // Later in your component:
 * const handleSubmit = async () => {
 *   await execute({ origin: 'Charleston, SC', destination: 'Atlanta, GA' });
 * };
 */
 //hook declaration with state declarations
export const useApi = (apiFunction) => {
    const [data, setData] = useState(null);
    const [loading, setLoading] = useState(false);
    const [error, setError] = useState(null);

    const execute = useCallback(
        async (...args) => {
            setLoading(true);
            setError(null);

            //calls api
            const [success, result] = await apiFunction(...args);
            //success handling
            if (success) {
                setData(result);
                setError(null);
            //error handling
            } else {
                setError(result);
                setData(null);
            }

            setLoading(false);
            return [success, result];
        },
        [apiFunction]
    );
    //clears all state
    const reset = useCallback(() => {
        setData(null);
        setError(null);
        setLoading(false);
    }, []);
    //returns object containing component needs
    return {
        data,
        loading,
        error,
        execute,
        reset,
    };
};

export default useApi;
