import axios, { AxiosResponse } from "axios";
import { Server } from "./sessionSlice";

export interface LoginRequest {
  username: string,
  password: string
}

export const login = (loginRequest: LoginRequest): Promise<AxiosResponse<{username: string, userId: number}>> => {
  return axios.post('/auth', loginRequest)
}

export const getChannels = (): Promise<AxiosResponse<Server[]>> => {
  return axios.get('/users/channels')
}