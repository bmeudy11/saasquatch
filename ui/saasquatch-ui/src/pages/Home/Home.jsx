import React, { useEffect, useState } from 'react';
import { Link } from 'react-router-dom';
import { Helmet } from 'react-helmet';
import { AiOutlineMail } from 'react-icons/ai';
import { SiInstagram, SiX} from 'react-icons/si';
import { FaSquareFacebook } from 'react-icons/fa6';
import logo from '../../assets/SaaSquatch-logo.png';
import placeholder from '../../assets/API.png';
import { IconContext } from 'react-icons/lib';
import './Home.scss';

// Define screen sizes
const screens = {
    small: window.matchMedia("all and (max-device-width: 640px)").matches,
    tabletPort: window.matchMedia("all and (min-device-width: 641px) and (max-device-width: 1024px) and (orientation: portrait)").matches,
    tabletLand: window.matchMedia("all and (min-device-width: 641px) and (max-device-width: 1024px) and (orientation: landscape)").matches,
    medium: window.matchMedia("all and (min-device-width: 1025px) and (max-device-width: 1919px)").matches,
    large: window.matchMedia("all and (min-device-width: 1920px) and (max-device-width: 2559px)").matches,
    xlarge: window.matchMedia("all and (min-device-width: 2560px)").matches,
};

export function Home(props) {
    // Get props and set state variables
    const { sideBarOpen } = props;
    const [screenSize, setScreenSize] = useState(screens);
    const [logoWidth, setLogoWidth] = useState(350);
    const [arcadeWidth, setArcadeWidth] = useState(300);

    useEffect(() => {
        const screenSizes = {
            small: window.matchMedia("all and (max-device-width: 640px)").matches,
            tabletPort: window.matchMedia("all and (min-device-width: 641px) and (max-device-width: 1024px) and (orientation: portrait)").matches,
            tabletLand: window.matchMedia("all and (min-device-width: 641px) and (max-device-width: 1024px) and (orientation: landscape)").matches,
            medium: window.matchMedia("all and (min-device-width: 1025px) and (max-device-width: 1919px)").matches,
            large: window.matchMedia("all and (min-device-width: 1920px) and (max-device-width: 2559px)").matches,
            xlarge: window.matchMedia("all and (min-device-width: 2560px)").matches,
        };
        const keys = Object.keys(screenSizes);

        const prev = {...screenSizes}
        const prevKeys = Object.keys(prev);
        for (let i = 0; i < prevKeys.length; i++) {
            if (screenSizes[keys[i]] !== prev[prevKeys[i]]) {
                prev[prevKeys[i]] = screenSizes[keys[i]];
            }
        }
        setScreenSize(prev);
    }, []);

    useEffect(() => {
        if (screenSize.large) {
            setLogoWidth(400);
            setArcadeWidth(350);
        } else if (screenSize.xlarge) {
            setLogoWidth(500);
            setArcadeWidth(450);
        } else {
            setLogoWidth(350);
            setArcadeWidth(300)
        }
    }, [screenSize]);

    return (
        <section id="home-section" style={{width: sideBarOpen ? '85%' : '97%'}}>
            <Helmet>
                <title>SaaSquatch | Home</title>
            </Helmet>

            <div id="home-wrapper-div" className="home-wrapper-div">
                <div id="home-content" className="home-content">
                    <div id="home-content-left">
                        <h1>Welcome to SaaSquatch!</h1>
                        <p>Slogan???</p>
                        <div>
                            <img src={placeholder} alt='image-placeholder' style={sideBarOpen ? {width: arcadeWidth} : {}} />
                        </div>
                        <Link to="/api-dashboard">
                            <button className="main-button">API</button>
                        </Link>
                    </div>
                    <div id="home-content-right" className={sideBarOpen ? 'open' : ''}>
                        <img src={logo} alt='logo.png' style={sideBarOpen ? {width: logoWidth} : {}} />
                        <div>
                            <Link to="/about">
                                <button className="main-button">About Us</button>
                            </Link>
                            <Link to="/contact">
                                <button className="main-button">Contact Us</button>
                            </Link>
                        </div>
                        <IconContext.Provider value={{className: 'link-icons'}}>
                            <h2 id="tags">#SaaSquatch &nbsp;
                                <a href="https://www.instagram.com/" target="_blank" rel="noreferrer">
                                    <SiInstagram />
                                </a> &nbsp;
                                <a href="https://www.facebook.com/" target="_blank" rel="noreferrer">
                                    <FaSquareFacebook />
                                </a> &nbsp;
                                <a href="https://twitter.com/" target="_blank" rel="noreferrer">
                                    <SiX />
                                </a> &nbsp;
                                <Link to="/contact">
                                    <AiOutlineMail />
                                </Link>
                            </h2>
                        </IconContext.Provider>
                    </div>
                </div>
            </div>
        </section>
    );
}
