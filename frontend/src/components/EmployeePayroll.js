import { useEffect, useState } from "react";
import {
    getMyPayroll,
    downloadMySalarySlip,
} from "../services/employeePayrollService";

function EmployeePayroll() {
    const [payrolls, setPayrolls] = useState([]);
    const [loading, setLoading] = useState(true);
    const [error, setError] = useState("");
    const [downloadingId, setDownloadingId] = useState(null);

    useEffect(() => {
        loadPayroll();
    }, []);

    const loadPayroll = async () => {
        try {
            const response = await getMyPayroll();
            setPayrolls(response.data);
        } catch (error) {
            console.error("Error fetching payroll:", error);
            setError("Unable to load payroll information.");
        } finally {
            setLoading(false);
        }
    };

    const handleDownloadSalarySlip = async (payroll) => {
        try {
            setDownloadingId(payroll.id);

            const response =
                await downloadMySalarySlip(payroll.id);

            const blob = new Blob(
                [response.data],
                { type: "application/pdf" }
            );

            const url =
                window.URL.createObjectURL(blob);

            const link =
                document.createElement("a");

            link.href = url;

            link.download =
                `salary-slip-${payroll.employee?.employeeCode || "employee"}-${payroll.month}.pdf`;

            document.body.appendChild(link);

            link.click();

            link.remove();

            window.URL.revokeObjectURL(url);

        } catch (error) {
            console.error(
                "Error downloading salary slip:",
                error
            );

            alert(
                "Unable to download salary slip."
            );
        } finally {
            setDownloadingId(null);
        }
    };

    if (loading) {
        return (
            <section>
                <h2>My Payroll</h2>
                <p>Loading payroll...</p>
            </section>
        );
    }

    if (error) {
        return (
            <section>
                <h2>My Payroll</h2>
                <p>{error}</p>
            </section>
        );
    }

    return (
        <section>
            <h2>My Payroll</h2>

            {payrolls.length === 0 ? (
                <p>No payroll records found.</p>
            ) : (
                <table>
                    <thead>
                        <tr>
                            <th>Month</th>
                            <th>Basic Salary</th>
                            <th>Allowances</th>
                            <th>Deductions</th>
                            <th>Net Salary</th>
                            <th>Payment Status</th>
                            <th>Salary Slip</th>
                        </tr>
                    </thead>

                    <tbody>
                        {payrolls.map((payroll) => (
                            <tr key={payroll.id}>
                                <td>
                                    {payroll.month}
                                </td>

                                <td>
                                    ₹{payroll.basicSalary}
                                </td>

                                <td>
                                    ₹{payroll.allowances || 0}
                                </td>

                                <td>
                                    ₹{payroll.deductions || 0}
                                </td>

                                <td>
                                    <strong>
                                        ₹{payroll.netSalary}
                                    </strong>
                                </td>

                                <td>
                                    {payroll.paymentStatus}
                                </td>

                                <td>
                                    <button
                                        onClick={() =>
                                            handleDownloadSalarySlip(
                                                payroll
                                            )
                                        }
                                        disabled={
                                            downloadingId ===
                                            payroll.id
                                        }
                                    >
                                        {downloadingId ===
                                        payroll.id
                                            ? "Downloading..."
                                            : "Download PDF"}
                                    </button>
                                </td>
                            </tr>
                        ))}
                    </tbody>
                </table>
            )}
        </section>
    );
}

export default EmployeePayroll;