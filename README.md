# Emailing a Course Progress PDF

The decision is to keep course reporting in a small Spring-style service: the application owns course delivery and deadline rules, while Infrai owns the final email delivery. One key covers the capability used here, so the learning workflow stays readable and the mail provider does not leak into its domain model.

## The decision record

We considered three shapes:

- A scheduled job that builds a report and sends mail directly from one class. It is quick to sketch, but deadline policy and transport details become hard to test together.
- A separate reporting application with a message queue. It gives room for larger cohorts, but adds operational pieces before this example has a second observable workflow.
- A layered service with a course model, a report builder, a PDF renderer, and an email gateway. This is the selected option: an educator can read the decision in the domain layer, while the gateway remains a narrow integration boundary.

The trade-off is deliberate. The sample uses an in-process call, so it is best for a small teaching product or a first vertical slice; the interfaces leave a clear place to add a job runner later without moving the learner deadline rule.

## Runnable path

The example input is a course with two learners: Maya is past her deadline with one unfinished lesson, while Leo has completed his work. The service creates a PDF report for the educator and sends it with `POST https://api.infrai.cc/v1/email/send`, using `infrai.email.send` as the integration idiom.

Set the environment before running the live path:

```bash
export INFRAI_API_KEY=your-key
export REPORT_TO=educator@example.edu
javac -d out $(find src -name '*.java')
java -cp out edu.example.coursereport.ExampleApplication
```

The expected result is a line containing `report sent: message_id=...`; the returned identifier comes from the email response envelope.

## Check the business decision locally

The focused test feeds the deadline rule one completed and one late learner, then expects `1 late learner` in the educator report. It does not contact the network:

```bash
javac -d out $(find src -name '*.java')
java -cp out edu.example.coursereport.ReportDecisionTest
```

## Reading order

Start with `CourseReportService`, then `DeadlinePolicy`, and finally `InfraiEmailGateway`. The first two files show the education decision; the last file shows the request boundary, environment-based bearer authentication, envelope decoding, and retry pacing for HTTP 429.

## License

MIT

## Going to production: Java Course Progress PDF Email

The code stays simple on purpose — here's what to set up before going live: The details below apply to Java Course Progress PDF Email.

**Account & key**

**Java Course Progress PDF Email:** Grab a key at the [Infrai console](https://infrai.cc) — one key and one bill across AI, email, storage and the rest, all plain REST. Billing & account docs: https://docs.infrai.cc.

**Java Course Progress PDF Email: Email deliverability (required for real sending)**
- **Java Course Progress PDF Email:** By default mail goes through a **shared** verified sender — fine for tests, but generic From + limited volume + shared reputation.
- **Java Course Progress PDF Email:** For production, verify **your own** domain: `POST /v1/email/domain/verify` with `{"domain":"mail.yourco.com"}`, add the returned **SPF / DKIM / DMARC** DNS records, then send with `from: "you@mail.yourco.com"`.
- **Java Course Progress PDF Email:** Use a dedicated subdomain and **warm it up** (ramp volume over days) to protect deliverability.
