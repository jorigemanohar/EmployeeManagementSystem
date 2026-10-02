import { useState } from "react";
import { addEmployee } from "../services/employeeService";

function AddEmployee({ onEmployeeAdded }) {
  const initialEmployee = {
    employeeCode: "",
    firstName: "",
    lastName: "",
    email: "",
    phone: "",
    designation: "",
    department: "",
    salary: "",
    joiningDate: "",
  };

  const [employee, setEmployee] = useState(initialEmployee);
  const [message, setMessage] = useState("");
  const [isSubmitting, setIsSubmitting] = useState(false);

  const handleChange = (event) => {
    const { name, value } = event.target;

    setEmployee((previousEmployee) => ({
      ...previousEmployee,
      [name]: value,
    }));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();

    setIsSubmitting(true);
    setMessage("Adding employee...");

    const employeeData = {
      ...employee,
      salary: employee.salary ? Number(employee.salary) : 0,
    };

    try {
      const response = await addEmployee(employeeData);

      console.log("Employee added:", response.data);

      // Reload employee list
      await onEmployeeAdded();

      setMessage("Employee added successfully!");

      // Clear form
      setEmployee(initialEmployee);
    } catch (error) {
      console.error("Error adding employee:", error);

      if (error.response) {
        console.error("Server response:", error.response.data);
        setMessage("Failed to add employee.");
      } else {
        setMessage("Unable to connect to the server.");
      }
    } finally {
      setIsSubmitting(false);
    }
  };

  return (
    <div className="add-employee">
      <h2>Add Employee</h2>

      {message && <p className="message">{message}</p>}

      <form onSubmit={handleSubmit}>
        <div className="form-group">
          <label>Employee Code</label>
          <input
            type="text"
            name="employeeCode"
            value={employee.employeeCode}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label>First Name</label>
          <input
            type="text"
            name="firstName"
            value={employee.firstName}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label>Last Name</label>
          <input
            type="text"
            name="lastName"
            value={employee.lastName}
            onChange={handleChange}
          />
        </div>

        <div className="form-group">
          <label>Email</label>
          <input
            type="email"
            name="email"
            value={employee.email}
            onChange={handleChange}
            required
          />
        </div>

        <div className="form-group">
          <label>Phone</label>
          <input
            type="text"
            name="phone"
            value={employee.phone}
            onChange={handleChange}
          />
        </div>

        <div className="form-group">
          <label>Designation</label>
          <input
            type="text"
            name="designation"
            value={employee.designation}
            onChange={handleChange}
          />
        </div>

        <div className="form-group">
          <label>Department</label>
          <input
            type="text"
            name="department"
            value={employee.department}
            onChange={handleChange}
          />
        </div>

        <div className="form-group">
          <label>Salary</label>
          <input
            type="number"
            name="salary"
            value={employee.salary}
            onChange={handleChange}
            min="0"
          />
        </div>

        <div className="form-group">
          <label>Joining Date</label>
          <input
            type="date"
            name="joiningDate"
            value={employee.joiningDate}
            onChange={handleChange}
          />
        </div>

        <button type="submit" disabled={isSubmitting}>
          {isSubmitting ? "Adding..." : "Add Employee"}
        </button>
      </form>
    </div>
  );
}

export default AddEmployee;