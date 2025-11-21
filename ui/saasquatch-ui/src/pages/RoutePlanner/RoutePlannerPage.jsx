import React, { useState, useEffect, useCallback } from 'react';
import { Helmet } from 'react-helmet';
import RouteForm from '../../components/RouteForm/RouteForm';
import {generateRandomDestination, getServerHealth} from '../../components/API/API';
import { v4 as uuidv4 } from 'uuid';
import './RoutePlannerPage.scss';

export function RoutePlannerPage(props) {
    const { sideBarOpen, alert } = props;
    const [loading, setLoading] = useState(false);
    const [generatedRoute, setGeneratedRoute] = useState();
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
        setLoading(true);
        const payload = {
            origin: data.originAddress,
            radius: data.radius,
        }
        const [success, result] = await generateRandomDestination(payload);
        if (success) {
            setGeneratedRoute(result);
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
        setLoading(false);
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
                            <h1>Destination Endpoints</h1>
                            <h4>(/destination/generateDestination)</h4>
                            <RouteForm
                                alert={alert}
                                onSubmit={submitHandler}
                                loading={loading}
                            />

                            {generatedRoute && (
                                <>
                                    <h3>Generated Route Data:</h3>
                                    <pre>{JSON.stringify(generatedRoute, null, 4)}</pre>
                                </>
                            )}
                        </div>
                    </div>
                </div>
            </div>
        </section>
    );
}
