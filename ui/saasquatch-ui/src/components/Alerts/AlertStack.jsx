import React from 'react';
import { Alert } from './Alert';
import './AlertStack.scss';

// Component to map and stack all alert messages
export function AlertStack(props) {
    // Get props
    const { alerts, close } = props;

    // Function to handle closing each alert message
    const closeHandler = (id) => {
        close(id);
    }

    return (
        <>
            <div id="alert-stack" className="alert-stack-div">
                {alerts.map((alert, key) => 
                    <Alert 
                        key={`${key}-${alert.id}`} 
                        id={alert.id} 
                        type={alert.type} 
                        message={alert.message} 
                        close={closeHandler} 
                        noClose={alert?.noClose}
                    />
                )}
            </div>
        </>
    );
}
