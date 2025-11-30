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
    const { type, message, close, noHeader, customHeader, width, slim } = props;
    const [success] = useState(type === 'success');
    const [error] = useState(type === 'error');
    const [warn] = useState(type === 'warn');
    const [info] = useState(type === 'info');

    // Function to handle closing the alert message
    const closeHandler = () => {
        close();
    };

    const BannerData = () => {
        const types = {
            success: <FaRegCheckCircle />,
            error: <MdErrorOutline />,
            warn: <IoWarningOutline />,
            info: <AiOutlineInfoCircle />
        };

        const activeType =
            success ? 'success' :
                error ? 'error' :
                    warn ? 'warn' :
                        info ? 'info' :
                            null;

        if (!activeType) return null;

        const Icon = types[activeType];

        if (!noHeader) {
            const headerText = customHeader
                ? customHeader
                : activeType === 'success'
                    ? 'Success!'
                    : activeType === 'error'
                        ? 'Error!'
                        : activeType === 'warn'
                            ? 'Warning!'
                            : 'Info';

            return (
                <>
                    <h2>{Icon} {headerText}</h2>
                    <p>{message}</p>
                </>
            );
        }

        return (
            <>
                {Icon} {message}
            </>
        );
    }

    return (
        <>
            <div className={`alert-banner ${type} ${slim ? 'slim' : ''}`} style={{width: width}}>
                {close && (
                    <div className="alert-banner-close" onClick={closeHandler}>
                        <MdClose />
                    </div>
                )}
                <IconContext.Provider value={{ className: 'alert-banner-icons' }}>
                    <BannerData />
                </IconContext.Provider>
            </div>
        </>
    );
}
