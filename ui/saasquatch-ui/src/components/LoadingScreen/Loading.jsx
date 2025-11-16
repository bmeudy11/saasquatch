import React from 'react';
import { Oval } from 'react-loading-icons';
import './Loading.scss';

export function Loading(props) {
    const { sideBarOpen } = props;

    return (
        <section id="loading-section" style={{width: sideBarOpen ? '85%' : '97%'}}>
            <div id="loading-wrapper-div" className="loading-wrapper-div">
                <div id="loading-content" className="loading-content">
                    <div>
                        <Oval />
                        <h1>Loading...</h1>
                    </div>
                </div>
            </div>
        </section>
    );
}
