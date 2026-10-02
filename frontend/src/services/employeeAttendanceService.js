
import api from "./api";

export const getMyAttendance = () =>
  api.get("/employee/attendance");
