import React, { useEffect, useState, useCallback } from 'react';
import { FaRegCheckCircle } from 'react-icons/fa';
import { MdClose, MdErrorOutline } from 'react-icons/md';
import { IoWarningOutline } from 'react-icons/io5';
import { AiOutlineInfoCircle } from 'react-icons/ai';
import { IconContext } from 'react-icons/lib';
import { timer } from '../../utils/Timer';
import './Alert.scss';

// Alert component
export function Alert(props) {
    // Get props and initalize state variables
    const { id, type, message, close, noClose } = props;
    const [success] = useState(type === 'success');
    const [error] = useState(type === 'error');
    const [warn] = useState(type === 'warn');
    const [info] = useState(type === 'info');

    // Function to handle closing the alert message
    const closeHandler = useCallback(() => {
        close(id);
    }, [close, id]);

    // useEffect to determine the type of alert message and timeout handler
    useEffect(() => {
        if (noClose !== true) {
            timer(5000).then(() => closeHandler())
        }
    }, [closeHandler, noClose]);

    return (
        <>
            <div id={`alert-${id}`} className={`alert ${type}`}>
                <IconContext.Provider value={{className: 'alert-icons'}}>
                    {success && <h2><FaRegCheckCircle /> Success!</h2>}
                    {error && <h2><MdErrorOutline /> Error!</h2>}
                    {warn && <h2><IoWarningOutline /> Warning!</h2>}
                    {info && <h2><AiOutlineInfoCircle /> Info</h2>}
                    <div id={`close-alert-${id}`} className="alert-close" onClick={closeHandler}>
                        <MdClose />
                    </div>
                    <p id={`alert-message-${id}`}>{message}</p>
                </IconContext.Provider>
            </div>
        </>
    );
}
