import React, { useState } from 'react';

import { useAppSelector, useAppDispatch } from '../../app/hooks';
import {
  loginAsync,
} from './sessionSlice';

export function Counter() {
  const status = useAppSelector(state => state.session.status);
  const token = useAppSelector(state => state.session.token)
  const dispatch = useAppDispatch();

  return (
    <div>
      Token: {token}
      Status: {status}
      <button onClick={() => dispatch(loginAsync({username: "user2", password: "pass2"}))}>Login</button>
    </div>
  );
}
