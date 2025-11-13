import { Navigate, Outlet } from 'react-router-dom';
import { useEffect, useState } from 'react';
// import { UseAuth } from '../FirebaseConfig';
import { Loading } from '../LoadingScreen/Loading';

export default function PrivateRoute(props) {
    // Get props and initalize state variables
    const { setUser, sideBarOpen } = props;
    // const user = UseAuth();
    const user = null;
    const intendedUrl = window.location.pathname.replace('/', '');
    const [index, setIndex] = useState(0);

    useEffect(() => {
        if (index < 5 && user) {
            setUser(user);
            setIndex(index + 1);
        }
    }, [index, setUser, user])

    return typeof user === 'undefined' ? (
        <Loading sideBarOpen={sideBarOpen} />
        ) : user ? (
         <Outlet />
        ) : (
            <Navigate to={`/login?redirect=${intendedUrl}`} />
        );
    
};

