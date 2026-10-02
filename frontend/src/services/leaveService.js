import api from "./api";

const API_URL = "/leaves";

export const getLeaves = () => api.get(API_URL);

export const addLeave = (leave) =>
  api.post(API_URL, leave);

export const updateLeave = (id, leave) =>
  api.put(`${API_URL}/${id}`, leave);

export const deleteLeave = (id) =>
  api.delete(`${API_URL}/${id}`);