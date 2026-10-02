function PayrollSummary({ payrolls }) {
    const totalNetPayroll = payrolls.reduce(
        (total, payroll) =>
            total + Number(payroll.netSalary || 0),
        0
    );

    const paidPayroll = payrolls
        .filter(
            (payroll) =>
                payroll.paymentStatus === "PAID"
        )
        .reduce(
            (total, payroll) =>
                total + Number(
                    payroll.netSalary || 0
                ),
            0
        );

    const pendingPayroll = payrolls
        .filter(
            (payroll) =>
                payroll.paymentStatus === "PENDING"
        )
        .reduce(
            (total, payroll) =>
                total + Number(
                    payroll.netSalary || 0
                ),
            0
        );

    return (
        <div className="payroll-summary">

            <h3>
                Payroll Analytics
            </h3>

            <div className="payroll-stats">

                <div className="payroll-stat">
                    <span>
                        Total Net Payroll
                    </span>

                    <strong>
                        ₹
                        {totalNetPayroll.toLocaleString(
                            "en-IN"
                        )}
                    </strong>
                </div>

                <div className="payroll-stat">
                    <span>
                        Paid Payroll
                    </span>

                    <strong>
                        ₹
                        {paidPayroll.toLocaleString(
                            "en-IN"
                        )}
                    </strong>
                </div>

                <div className="payroll-stat">
                    <span>
                        Pending Payroll
                    </span>

                    <strong>
                        ₹
                        {pendingPayroll.toLocaleString(
                            "en-IN"
                        )}
                    </strong>
                </div>

                <div className="payroll-stat">
                    <span>
                        Payroll Records
                    </span>

                    <strong>
                        {payrolls.length}
                    </strong>
                </div>

            </div>

        </div>
    );
}

export default PayrollSummary;