import axios, { AxiosResponse } from "axios";

export interface LoginRequest {
  username: string,
  password: string
}

export const login = (loginRequest: LoginRequest): Promise<AxiosResponse<string>> => {
  return axios.post('/auth', loginRequest)
}