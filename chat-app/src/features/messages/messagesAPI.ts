import axios, { AxiosResponse } from "axios";
import { Msg } from "./messagesSlice";

export const getMessages = (channelId: number, timestamp: number, amount: number): Promise<AxiosResponse<Msg[]>> => {
  return axios.get(`/channels/${channelId}/messages?timestamp=${timestamp}&number=${amount}`)
}