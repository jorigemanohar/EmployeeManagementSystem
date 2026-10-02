import api from "./api";

const API_URL = "/payroll";

export const getPayrolls = () => api.get(API_URL);

export const addPayroll = (payroll) =>
  api.post(API_URL, payroll);

export const updatePayroll = (id, payroll) =>
  api.put(`${API_URL}/${id}`, payroll);

export const deletePayroll = (id) =>
  api.delete(`${API_URL}/${id}`);