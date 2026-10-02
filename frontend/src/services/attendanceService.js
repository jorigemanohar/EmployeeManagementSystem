import api from "./api";

const API_URL = "/attendance";

export const getAttendance = () => api.get(API_URL);

export const addAttendance = (attendance) =>
  api.post(API_URL, attendance);

export const updateAttendance = (id, attendance) =>
  api.put(`${API_URL}/${id}`, attendance);

export const deleteAttendance = (id) =>
  api.delete(`${API_URL}/${id}`);