import api from "./api";

export const getMyPayroll = () => {
    return api.get("/employee/payroll");
};

export const downloadMySalarySlip = (id) => {
    return api.get(`/employee/payroll/${id}/pdf`, {
        responseType: "blob",
    });
};