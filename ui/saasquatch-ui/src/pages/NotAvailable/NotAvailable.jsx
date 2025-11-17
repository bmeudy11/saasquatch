import React from 'react';
import { Helmet } from 'react-helmet';
import image from '../../assets/404.png';
import './NotAvailable.scss';

export function NotAvailable(props) {
    const { sideBarOpen } = props;

    return (
        <section id="not-available-section" style={{width: sideBarOpen ? '85%' : '97%'}}>
            <Helmet>
                <title>SaaSquatch | Page Not Found</title>
            </Helmet>
            <div id="not-available-wrapper-div" className="not-available-wrapper-div">
                <div id="not-available-content" className="not-available-content">
                    <div>
                        <img src={image} alt='not-available.png' />
                    </div>
                    <h1>Oops!</h1>
                    <p>Looks like you're trying to view on a mobile device</p>
                    <p>Mobile devices are not supported at this time.</p>
                    <p id="check-back">Please check back later!</p>
                </div>
            </div>
        </section>
    );
}
