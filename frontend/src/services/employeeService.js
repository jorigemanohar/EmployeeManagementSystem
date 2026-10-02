import api from "./api";

const API_URL = "/employees";

// =========================================================
// GET ALL EMPLOYEES
// =========================================================

export const getEmployees = () => {
  return api.get(API_URL);
};

// =========================================================
// ADD EMPLOYEE
// =========================================================

export const addEmployee = (employee) => {
  return api.post(API_URL, employee);
};

// =========================================================
// UPDATE EMPLOYEE
// =========================================================

export const updateEmployee = (
  id,
  employee
) => {
  return api.put(
    `${API_URL}/${id}`,
    employee
  );
};

// =========================================================
// DELETE EMPLOYEE
// =========================================================

export const deleteEmployee = (id) => {
  return api.delete(
    `${API_URL}/${id}`
  );
};

// =========================================================
// CREATE EMPLOYEE LOGIN ACCOUNT
// =========================================================

export const createEmployeeAccount = (
  employeeId,
  account
) => {
  return api.post(
    `/auth/create-employee-account/${employeeId}`,
    account
  );
};