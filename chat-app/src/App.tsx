import React from 'react';
import './App.css';
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Link
} from "react-router-dom";
import { useAppDispatch, useAppSelector } from './app/hooks';
import { Chat } from './components/Chat';
import { Login } from './components/Login';
import { Home } from './components/Home';
import { logoutAction } from './features/session/sessionSlice';
import axios from 'axios';

function App() {
  const username = useAppSelector(state => state.session.user?.username)
  const status = useAppSelector(state => state.session.status)
  const token = useAppSelector(state => state.session.token)

  const dispatch = useAppDispatch()

  if(token != null){
    axios.interceptors.request.use(function (config) {
      config.headers.Authorization = token;
      return config;
    }, function (error) {
      return Promise.reject(error);
    });
  }

  const logout = () => {
    dispatch(logoutAction())
  }

  return (
    <Router>
      <div>
        <nav>
          <ul>
            <li>
              <Link to="/">Home</Link>
            </li>
            <li>
              <Link to="/chat">Chat</Link>
            </li>
            {status == 'none' &&
              <li>
                <Link to="/login">Login</Link>
              </li>
            }
            {status == 'active' &&
              <li>
                Witaj, {username}
              </li>
            }
            {status == 'active' &&
              <li>
                <button onClick={logout}>Logout</button>
              </li>
            }
          </ul>
        </nav>

        <Switch>
          <Route path="/chat">
            <Chat />
          </Route>
          <Route path="/login">
            <Login />
          </Route>
          <Route path="/">
            <Home />
          </Route>
        </Switch>
      </div>
    </Router>
  );
}

export default App;
