import { createAsyncThunk, createSlice, PayloadAction } from '@reduxjs/toolkit';
import { login, LoginRequest } from './sessionAPI';

export interface Channel {
  name: string,
  id: number
}

export interface Server {
  name: string,
  id: number,
  channels: Channel[]
}

export interface User {
  username: string,
  id: number
}

export interface Connection {
  status: 'active' | 'connecting' | 'disconnected',
  serverId: number,
  currentChannelId: number
}

export interface SessionState {
  status: 'loading' | 'none' | 'active',
  token: string | null,
  user: User | null,
  servers: Server[],
  connection: Connection | null
}

const initialState: SessionState = {
  status: 'none',
  token: null,
  user: null,
  servers: [],
  connection: null
};

export const loginAsync = createAsyncThunk(
  'session/login',
  async (loginRequest: LoginRequest): Promise<string | null> => {
    const response = await login(loginRequest);
    if(response.data === "ok")
      return response.headers["authorization"];
    else
      return null;
  }
);

export const sessionSlice = createSlice({
  name: 'session',
  initialState,
  reducers: {
    logout: (state) => {
      state = initialState
    }
  },
  extraReducers: (builder) => {
    builder
      .addCase(loginAsync.pending, (state) => {
        state.status = 'loading';
      })
      .addCase(loginAsync.fulfilled, (state, action) => {
        if(action.payload != null){
          state.status = 'active';
          state.token = action.payload;
        }
      });
  },
});

export const { } = sessionSlice.actions;

export default sessionSlice.reducer;
