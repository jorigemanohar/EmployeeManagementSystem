import api from "./api";

export const getMyLeaves = () => {
    return api.get("/employee/leaves");
};

export const submitLeave = (leave) => {
    return api.post("/employee/leaves", leave);
};