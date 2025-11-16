import React, { useCallback, useEffect, useState } from 'react';
import { BrowserRouter as Router, Routes, Route, Navigate } from 'react-router-dom';
import { AlertStack } from './components/Alerts/AlertStack';
import { Sidebar } from './components/Sidebar/Sidebar';
import { Home } from './pages/Home/Home';
import { NotFound } from './pages/NotFound/NotFound';
// import PrivateRoute from './components/PrivateRoute/PrivateRoute';
// import { Account } from './pages/Account/Account';
import './App.scss';
import {NotAvailable} from "./pages/NotAvailable/NotAvailable";

// Define screen sizes
const screens = {
    small: window.matchMedia("all and (max-device-width: 640px)").matches,
    tabletPort: window.matchMedia("all and (min-device-width: 641px) and (max-device-width: 1024px) and (orientation: portrait)").matches,
    tabletLand: window.matchMedia("all and (min-device-width: 641px) and (max-device-width: 1024px) and (orientation: landscape)").matches,
    medium: window.matchMedia("all and (min-device-width: 1025px) and (max-device-width: 1919px)").matches,
    large: window.matchMedia("all and (min-device-width: 1920px) and (max-device-width: 2559px)").matches,
    xlarge: window.matchMedia("all and (min-device-width: 2560px)").matches,
};

function App() {
    const [screenSize, setScreenSize] = useState(screens);
    const [sideBarOpen, setSideBarOpen] = useState(false);
    const [alerts, setAlerts] = useState([]);
    // eslint-disable-next-line
    const [user, setUser] = useState(undefined);

    // Function to handle opening alert messages
    const openAlertHandler = useCallback((newAlert) => {
        const alertList = [...alerts];
        if (newAlert instanceof Array) {
            for (let i = 0; i < newAlert.length; i++) {
                alertList.push(newAlert[i]);
            }
        } else {
            alertList.push(newAlert);
        }
        setAlerts(alertList);
    }, [alerts]);

    // Function to handle closing alert messages
    const closeAlertHandler = useCallback((id) => {
        const alertList = [...alerts];
        for (let i = 0; i < alertList.length; i++) {
            if (alertList[i].id === id) {
                alertList.splice(i, 1);
            }
        }
        setAlerts(alertList);
    }, [alerts]);

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

    // const userHandler = (newUser) => {
    //     setUser(newUser);
    // };

    if (!screenSize.small && !screenSize.tabletPort && !screenSize.tabletLand) {
        return (
            <Router>
                <main>
                    <Sidebar
                        alert={openAlertHandler}
                        user={user}
                        isOpen={sideBarOpen}
                        // setUser={userHandler}
                        changeOpen={() => setSideBarOpen(!sideBarOpen)}
                    />
                    <Routes>
                        <Route path="/" element={<Navigate to="/home"/>}/>
                        <Route path="/home" element={<Home sideBarOpen={sideBarOpen} />} />
                        {/* Implement this after auth is complete - protects certain pages from unauthorized access */}
                        {/*<Route element={<PrivateRoute user={user} setUser={userHandler} sideBarOpen={sideBarOpen} />}>*/}
                        {/*    <Route path={"/account"} element={<Account sideBarOpen={sideBarOpen} user={user} />} />*/}
                        {/*</Route>*/}
                        {/*<Route path="/login" element={<Login sideBarOpen={sideBarOpen} alert={openAlertHandler} />} />*/}
                        <Route path="*" element={<NotFound sideBarOpen={sideBarOpen} />} />
                    </Routes>
                    <AlertStack alerts={alerts} close={closeAlertHandler} />
                </main>
            </Router>
        );
    }
    // Viewable on desktops/laptops only
    return (
        <main><NotAvailable /></main>
    )
}

export default App;
