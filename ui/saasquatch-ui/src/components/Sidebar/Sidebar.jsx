import React from 'react';
import { Link } from 'react-router-dom';
import { TfiArrowCircleLeft } from 'react-icons/tfi';
import { AiFillHome, AiOutlineMessage } from 'react-icons/ai';
import { BsInfoCircle } from 'react-icons/bs';
import { FaBell } from 'react-icons/fa';
import { IconContext } from 'react-icons/lib';
import { LuBellElectric } from 'react-icons/lu';
import { MdAccountCircle } from 'react-icons/md';
import logo from '../../assets/SaaSquatch-logo.png';
import pfp from '../../assets/image-placeholder.jpeg';
import { v4 as uuidv4 } from 'uuid';
// import { sendLogOut } from '../../api/firebaseApi';
import './Sidebar.scss';

// Component for sidebar navigation
export function Sidebar(props) {
    // Get props and initialize state variable
    const { alert, isOpen, changeOpen, user } = props;
    // const { username, alert, isOpen, changeOpen, user, setUser } = props;

    // Function to handle opening and closing the sidebar
    const openerHandler = () => {
        changeOpen();
    };

    // TODO: Remove after scaffolding story
    const alertStackTestHandler = () => {
        const alertPayload = [
            {
                id: uuidv4(),
                type: 'info',
                message: 'This is an INFO alert',
            },
            {
                id: uuidv4(),
                type: 'warn',
                message: 'This is an WARNING alert',
            },
            {
                id: uuidv4(),
                type: 'error',
                message: 'This is an ERROR alert',
            },
            {
                id: uuidv4(),
                type: 'success',
                message: 'This is an SUCCESS alert',
            },
        ];
        alert(alertPayload);
    }

    // TODO: Remove after scaffolding story
    const alertTestHandler = () => {
        const alertPayload = {
            id: uuidv4(),
            type: 'success',
            message: 'This is an SUCCESS alert',
        };
        alert(alertPayload);
    }

    // Function to handle log out
    const logOutHandler = async () => {
        const alertPayload = {
            id: uuidv4(),
            type: 'info',
            message: 'This function is not yet available',
        };
        alert(alertPayload);
        // const loggedOut = await sendLogOut();
        // if (loggedOut[0]) {
        //     const alertPayload = {
        //         id: uuidv4(),
        //         type: 'success',
        //         message: 'You have been successfully logged out.',
        //     };
        //     setUser(undefined);
        //     alert(alertPayload);
        // } else {
        //     const alertPayload = {
        //         id: uuidv4(),
        //         type: 'error',
        //         message: `${loggedOut[1]}`,
        //     };
        //     alert(alertPayload);
        // }
    };

    return (
        <section id="sidebar" style={{width: isOpen ? '15%' : '3%'}} className={isOpen ? '' : 'glass'}>
            <div id="nav-control-div" className={`nav-control-div ${isOpen ? 'closed' : ''}`}>
                <IconContext.Provider value={{className: `arrow ${isOpen ? 'rotate' : ''}`}}>
                    {isOpen ? <img src={logo} alt="logo.png" /> : <></>}
                    <h1 className="logo-font">
                        <TfiArrowCircleLeft style={{fontSize: 25}} onClick={openerHandler} />
                    </h1>
                </IconContext.Provider>
            </div>
            <hr style={{margin: isOpen ? '1.5em 1em 20px 1em' : '1.5em 7px 5px 7px'}} />
            {isOpen && (
                <div id="nav-list">
                        <div id="account-card" className="account-card">
                            <Link to={`/${'account'}`}>
                                <img src={pfp} alt="task-master-logo.png" />
                            </Link>
                            {!!user
                                ? <span>
                                    <h5>
                                        {user?.displayName} • <Link to="#" onClick={logOutHandler}>Log Out</Link>
                                    </h5>
                                </span>
                                : <span>
                                    <Link to="/login">Log In</Link> / <Link to="/create-account">Create</Link>
                                </span>
                            }
                        </div>
                    <Link to="/home">
                        <div id="home-nav" className="nav-button">
                            <AiFillHome id="home-nav-icon" />
                            <span>Home</span>
                        </div>
                    </Link>
                    <Link to="/about">
                        <div id="about-nav" className="nav-button">
                            <BsInfoCircle id="about-nav-icon" />
                            <span>About</span>
                        </div>
                    </Link>
                    <Link to="/contact">
                        <div id="contact-nav" className="nav-button">
                            <AiOutlineMessage id="contact-nav-icon" />
                            <span>Contact Us</span>
                        </div>
                    </Link>
                    <Link to="#" onClick={alertStackTestHandler}>
                        <div id="alert-stack-nav" className="nav-button">
                            <LuBellElectric id="alert-stack-nav-icon" />
                            <span>Alert Stack</span>
                        </div>
                    </Link>
                    <Link to="#" onClick={alertTestHandler}>
                        <div id="alert-nav" className="nav-button">
                            <FaBell id="alert-nav-icon" />
                            <span>Alert</span>
                        </div>
                    </Link>
                    <Link to="report-a-problem-form">
                        <div id="report-a-problem-link" className="nav-link">
                            <p>Report a Problem</p>
                        </div>
                    </Link>
                    <div id="version-number" className="build">
                        <p>v{process.env.REACT_APP_VERSION}</p>
                    </div>
                </div>
            )}

            {!isOpen && (
                <div id="nav-list" className="nav">
                    <IconContext.Provider value={{className: 'nav-icons'}}>
                        <Link to='/account'>
                            <MdAccountCircle />
                        </Link>
                        <hr style={{margin: '2.5px 7px'}} />
                        <Link to="/home">
                            <AiFillHome id="home-nav-icon" />
                        </Link>
                        <Link to="/about">
                            <BsInfoCircle id="about-nav-icon" />
                        </Link>
                        <Link to="/contact">
                            <AiOutlineMessage id="contact-nav-icon" />
                        </Link>
                        <Link to="#" onClick={alertStackTestHandler}>
                            <LuBellElectric id="alert-stack-nav-icon" />
                        </Link>
                        <Link to="#" onClick={alertTestHandler}>
                            <FaBell id="alert-nav-icon" />
                        </Link>
                    </IconContext.Provider>
                </div>
            )}
        </section>
    );
}
