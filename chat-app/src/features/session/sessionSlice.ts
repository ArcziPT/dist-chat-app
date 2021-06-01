import { createAsyncThunk, createSlice, PayloadAction } from '@reduxjs/toolkit';
import { getChannels, login, LoginRequest } from './sessionAPI';

export interface Channel {
  role: string,
  name: string,
  channelId: number,
  serverId: number
}

export interface Server {
  name: string,
  serverId: number,
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
  async (loginRequest: LoginRequest): Promise<{username: string, userId: number, token: string} | null> => {
    const response = await login(loginRequest);
    return {...response.data, token: response.headers["authorization"]}
  }
);

export const getChannelsAsync = createAsyncThunk(
  'session/getChannels',
  async (): Promise<Server[]> => {
    const response = await getChannels();
    return response.data;
  }
);

export const sessionSlice = createSlice({
  name: 'session',
  initialState,
  reducers: {
    logoutAction: (state) => {
      state.user = initialState.user
      state.token = initialState.token
      state.status = initialState.status
      state.servers = initialState.servers
      state.connection = initialState.connection
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
          state.user = {
            username: action.payload.username,
            id: action.payload.userId
          }
          state.token = action.payload.token;
        }
      })
      .addCase(getChannelsAsync.fulfilled, (state, action) => {
        state.servers = action.payload
      });
  },
});

export const { logoutAction } = sessionSlice.actions;

export default sessionSlice.reducer;
