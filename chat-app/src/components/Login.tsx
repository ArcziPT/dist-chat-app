import React, { useState } from 'react';
import { useSelector } from 'react-redux';
import { useHistory } from 'react-router';
import { useAppDispatch, useAppSelector } from '../app/hooks';
import { loginAsync } from '../features/session/sessionSlice';

export const Login = () => {
    const [username, setUsername] = useState('')
    const [password, setPassword] = useState('')
    const dispatch = useAppDispatch()
    const history = useHistory()

    const loginStatus = useAppSelector(state => state.session.status)

    if(loginStatus == 'active')
        history.push('/chat')

    const passwordChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        event.preventDefault()
        setPassword(event.target.value)
    }

    const usernameChange = (event: React.ChangeEvent<HTMLInputElement>) => {
        event.preventDefault()
        setUsername(event.target.value)
    }

    const login = () => {
        dispatch(loginAsync({username, password}))
    }

    return (
        <div>
            Status: {loginStatus}
            <form>
            <p>Username:</p>
            <input
            type='text'
            onChange={usernameChange}
            />
            <p>Password:</p>
            <input
            type='password'
            onChange={passwordChange}
            />
            </form>
            <button onClick={login}>
                Login
            </button>
        </div>
    );
}