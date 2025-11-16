import { ChangeEvent, FormEvent, useState } from 'react';
import { v4 as uuidv4 } from 'uuid';
import {
    Box,
    Card,
    CardActions,
    CardContent,
    CardHeader,
    IconButton,
    InputAdornment,
    TextField,
} from '@mui/material';
import { BiShow, BiHide, BiLogIn } from 'react-icons/bi';
import { IoIosMail } from 'react-icons/io';
import { FaKey } from 'react-icons/fa';
// import { loginUser } from '../../API/API';
import { useNavigate, useLocation } from 'react-router-dom';

export function LoginForm(props: { alert: Function;}) {

    // Initialize form state
    const initialFormState = {
        email: '',
        password: '',
        resetEmail: '',
    };

    // Form types
    type FormState = {
        email: string;
        password: string;
        resetEmail: string;
    };

    // Get the component props and initialize useStates
    const { alert } = props;
    const [formState, setFormState] = useState<FormState>(initialFormState);
    const [submitting, setSubmitting] = useState<boolean>(false);
    const [isSelected1, setIsSelected1] = useState<boolean>(false);
    const [isSelected2, setIsSelected2] = useState<boolean>(false);
    const [showPassword, setShowPassword] = useState<boolean>(false);
    const history = useNavigate();
    const location = useLocation();

    // Function to show a message from form
    const showAlert = (alertPayload: object) => {
        alert(alertPayload);
    };

    // Function to submit form
    const submitForm = async (event: FormEvent) => {
        event.preventDefault();
        setSubmitting(true);
        const logIn = [false, 'Function unavailable'];
        // const logIn:Array<boolean | string | null> = await loginUser(formState.email, formState.password);
        if (!logIn[0]) {
            // Show floating error alert message
            const message = {
                id: uuidv4(),
                type: 'error',
                message: logIn[1],
            };
            showAlert(message);
        } else {
            // Show floating success alert message
            const message = {
                id: uuidv4(),
                type: 'success',
                message: 'Log in successful! Welcome back!',
            };
            showAlert(message);

            // Check if there is a redirect parameter in the URL
            const redirectUrl = new URLSearchParams(location.search).get('redirect');
            if (redirectUrl) {
                // Use the history object to navigate to the intended URL
                history(`/${redirectUrl}`);
            } else {
                history('/games');
            }
        }
        setSubmitting(false);
    };

    // Update form text field
    const updateFormControl = (event: ChangeEvent<HTMLInputElement | HTMLTextAreaElement>) => {
        const { id, value } = event.target;
        const key = id as keyof FormState;
        const updatedFormState = { ...formState };
        updatedFormState[key] = value;
        setFormState(updatedFormState);
    };

    // Show password click handler
    const handleClickShowPassword = () => setShowPassword(!showPassword);
    const handleMouseDownPassword = () => setShowPassword(!showPassword);

    // Form title
    const title = <><BiLogIn /> Log In</> ;

    // Email adornment
    const emailAdornment = isSelected1
        ? {
            startAdornment: (
                <InputAdornment position="start">
                    <IoIosMail />
                </InputAdornment>
            )
        } : {};

    // Password Adornments
    const passAdornment = isSelected2
        ? {
            startAdornment: (
                <InputAdornment position="start">
                    <FaKey />
                </InputAdornment>
            ),
            endAdornment: (
                <InputAdornment position="end">
                    <IconButton
                        aria-label="toggle password visibility"
                        onClick={handleClickShowPassword}
                        onMouseDown={handleMouseDownPassword}
                    >
                        {showPassword ? <BiShow /> : <BiHide />}
                    </IconButton>
                </InputAdornment>
            )
        } : {
            endAdornment: (
                <InputAdornment position="end">
                    <IconButton
                        aria-label="toggle password visibility"
                        onClick={handleClickShowPassword}
                        onMouseDown={handleMouseDownPassword}
                    >
                        {showPassword ? <BiShow /> : <BiHide />}
                    </IconButton>
                </InputAdornment>
            )
        }
    ;

    return (
        <>
            <div id="form">
                <Box sx={{display: 'inline-block'}}>
                    <Card className="MuiCard-root form-card">
                        <CardHeader title={title} />

                        {/* Form */}
                        <form onSubmit={submitForm}>
                            <CardContent>
                                {/* Email */}
                                <div className="padded">
                                    <TextField
                                        required
                                        id="email"
                                        label="Email"
                                        variant="outlined"
                                        type="email"
                                        onChange={updateFormControl}
                                        value={formState?.email}
                                        slotProps={{input: emailAdornment, inputLabel: {className: 'hidden-asterisk'}}}
                                        onFocus={e => setIsSelected1(true)}
                                        onBlur={formState.email ? undefined : e => setIsSelected1(false)}
                                        onInput={e => setIsSelected1(true)}
                                    />
                                </div>

                                {/* Password */}
                                <div className="padded">
                                    <TextField
                                        required
                                        id="password"
                                        label="Password"
                                        variant="outlined"
                                        type={showPassword ? "text" : "password"}
                                        onChange={updateFormControl}
                                        value={formState?.password}
                                        slotProps={{input: passAdornment, inputLabel: {className: 'hidden-asterisk'}}}
                                        onFocus={e => setIsSelected2(true)}
                                        onBlur={formState.password ? undefined : e => setIsSelected2(false)}
                                        onInput={e => setIsSelected2(true)}
                                    />
                                </div>
                            </CardContent>

                            {/* Submit Actions */}
                            <CardActions className="card-actions">
                                <button className="main-button" disabled={submitting} style={{marginTop: '-30px'}}>
                                    {submitting ? 'Logging In...' : 'Log In'}
                                </button>
                            </CardActions>
                        </form>
                    </Card>
                </Box>
            </div>
        </>
    );
}
