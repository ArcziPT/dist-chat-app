import React from 'react';
import './App.css';
import {
  BrowserRouter as Router,
  Switch,
  Route,
  Link
} from "react-router-dom";
import { useAppSelector } from './app/hooks';
import { Chat } from './components/Chat';
import { Login } from './components/Login';
import { Home } from './components/Home';

function App() {
  const username = useAppSelector(state => state.session.user?.username)
  const status = useAppSelector(state => state.session.status)

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
