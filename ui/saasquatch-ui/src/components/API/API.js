import axios from 'axios';

const BASE_URL = process.env.REACT_APP_BASE_URL;

export const exampleGetEndpoint = async () => {
    let returnPayload = [true, null];

    try {
        const url = `${BASE_URL}/exampleGet`;
        const response = await axios.get(url, {
            headers: {},
        });
        returnPayload = [true, response.data];
    } catch (e) {
        console.error(e);
        returnPayload = [false, e.response?.data?.error || e.message];
    } finally {
        return returnPayload;
    }
};

export const examplePostEndpoint = async (fakeData) => {
    try {
        const response = await axios.post(
            `${BASE_URL}/examplePost`,
            { fakeData },
            { headers: {} },
        );
        return [true, response.data];
    } catch (e) {
        console.error(e);
        return [false, e.response?.data?.error || e.message];
    }
}
