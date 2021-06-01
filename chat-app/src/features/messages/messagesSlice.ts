import { createAsyncThunk, createSlice, PayloadAction } from '@reduxjs/toolkit';
import { getMessages } from './messagesAPI';

export interface Msg{
  username: string,
  userId: number,
  timestamp: number,
  channelId: number,
  text: string
}

export interface MessagesState {
  [x: string]: Msg[]
}

const initialState: MessagesState = {};

export interface MsgRequest {
  channelId: number,
  timestamp: number,
  amount: number
}

export const getMessagesAsync = createAsyncThunk(
  'messages/get',
  async (msgRequest: MsgRequest): Promise<Msg[]> => {
    const response = await getMessages(msgRequest.channelId, msgRequest.timestamp, msgRequest.amount);
    return response.data;
  }
);

export const messagesSlice = createSlice({
  name: 'messages',
  initialState,
  reducers: {
    addMessage: (state, action) => {
      if(state.hasOwnProperty(action.payload.channelId))
        state[action.payload.channelId].push(action.payload.msg)
      else
      state[action.payload.channelId] = [action.payload.msg]
    }
  },
  extraReducers: (builder) => {
    builder
      .addCase(getMessagesAsync.fulfilled, (state, action) => {
        if(action.payload.length > 0){
          const channelId = action.payload[0].channelId.toString()

          if(state.hasOwnProperty(channelId)){
            state[channelId].concat(action.payload)
            state[channelId] = state[channelId].sort((m1, m2) => m1.timestamp - m2.timestamp)
          }else{
            state[channelId] = action.payload
          }
        }
      });
  },
});

export const { addMessage } = messagesSlice.actions;

export default messagesSlice.reducer;
