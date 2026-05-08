import { SESClient, SendEmailCommand } from "@aws-sdk/client-ses";

const sesClient = new SESClient({ region: "us-east-1" });

export const handler = async (event) => {
  const htmlBody = `
    <div style="font-family: sans-serif; padding: 20px;">
        <h2>Order Shipped!</h2>
        <p>Your order is shipped to your <strong>registered address</strong>.</p>
        <p>Thank you for shopping with us!</p>
    </div>
`;

  for (const record of event.Records) {
    const notificationMessage = JSON.parse(record.body);
    const recipientEmail = notificationMessage.toAddress;

    const batchItemFailures = [];

    try {

      if (recipientEmail === "" || recipientEmail === null) {
        throw new Error(`Missing requiredField in message: ${record.messageId}`);
      }

      const params = {
        Source: "naveendesaiaws@gmail.com", // Must be verified in SES
        Destination: {
          ToAddresses: [recipientEmail],
        },
        Message: {
          Subject: {
            Data: "Your Order is Shipped!",
            Charset: "UTF-8",
          },
          Body: {
            Text: {
              Data: "Your order is shipped to registered address. Thank you for shopping with us.",
              Charset: "UTF-8",
            },
            Html: {
              Data: htmlBody,
              Charset: "UTF-8",
            },
          },
        },
      };
      await sesClient.send(new SendEmailCommand(params));
    } catch (err) {
      console.error("Failed to send email to", recipientEmail, err);
    }

  };

  return { batchItemFailures };;
};
