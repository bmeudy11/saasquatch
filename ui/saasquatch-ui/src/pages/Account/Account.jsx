import React from 'react';
import { Helmet } from 'react-helmet';
import './Account.scss';

export function Account(props) {
    const { sideBarOpen, user } = props;

    return (
        <section id="account-section" style={{width: sideBarOpen ? '85%' : '97%'}}>
            <Helmet>
                <title>SaaSquatch | Account</title>
            </Helmet>
            <div id="account-wrapper-div" className="account-wrapper-div">
                <div id="account-content" className="account-content">
                    <div className="info-message">
                        <h2>Account Info</h2>
                        <p>
                            <span>UID:</span> {user?.uid}
                        </p>
                        <p>
                            <span>USERNAME:</span> {user?.displayName}
                        </p>
                        <p>
                            <span>EMAIL:</span> {user?.email}
                        </p>
                    </div>
                </div>
            </div>
        </section>
    );
}
