import api from "./api";

const API_URL = "/departments";

export const getDepartments = () => api.get(API_URL);

export const addDepartment = (department) =>
  api.post(API_URL, department);

export const updateDepartment = (id, department) =>
  api.put(`${API_URL}/${id}`, department);

export const deleteDepartment = (id) =>
  api.delete(`${API_URL}/${id}`);