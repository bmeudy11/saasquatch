import React, { useState } from 'react';
import { FaRegCheckCircle } from 'react-icons/fa';
import { MdClose, MdErrorOutline } from 'react-icons/md';
import { IoWarningOutline } from 'react-icons/io5';
import { AiOutlineInfoCircle } from 'react-icons/ai';
import { IconContext } from 'react-icons/lib';
import './AlertBanner.scss';

// Alert banner component
export function AlertBanner(props) {
    // Get props and initalize state variables
    const { type, message, close, noHeader } = props;
    const [success] = useState(type === 'success');
    const [error] = useState(type === 'error');
    const [warn] = useState(type === 'warn');
    const [info] = useState(type === 'info');

    // Function to handle closing the alert message
    const closeHandler = () => {
        close();
    };

    return (
        <>
            <div className={`alert-banner ${type}`}>
                {close && (
                    <div className="alert-banner-close" onClick={closeHandler}>
                        <MdClose />
                    </div>
                )}
                <IconContext.Provider value={{className: 'alert-banner-icons'}}>
                    {(!noHeader && success) && <h2><FaRegCheckCircle /> Success!</h2>}
                    {(!noHeader && error) && <h2><MdErrorOutline /> Error!</h2>}
                    {(!noHeader && warn) && <h2><IoWarningOutline /> Warning!</h2>}
                    {(!noHeader && info) && <h2><AiOutlineInfoCircle /> Info</h2>}
                    {!noHeader && <p>{message}</p>}

                    {(noHeader && success) && <><FaRegCheckCircle /> {message}</>}
                    {(noHeader && error) && <><MdErrorOutline /> {message}</>}
                    {(noHeader && warn) && <><IoWarningOutline /> {message}</>}
                    {(noHeader && info) && <><AiOutlineInfoCircle /> {message}</>}
                </IconContext.Provider>
            </div>
        </>
    );
}
