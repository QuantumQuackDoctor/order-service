Order Service-

Responsibilities: handle operations (CRUD) regarding orders including linking orders to users and sending confirmation
emails for new order creations.

Endpoints: 
user endpoints
/order -put (creating orders), delete (deleting orders), patch (update orders)
/order/user -get (get user specific orders)
/order/charge - post (create payment charge for stripe)
/order/email-order -post (if order is successfully place, send confirmation email if notification is on)
/order/driver -put (driver accept order), patch (driver update order), delete (driver cancel order),
		get (driver get accepted orders)

admin end points
/orders -get (admin get orders), delete (admin delete orders)

Necessary environment variables
This service uses the AWS SDK to send order detail emails through SES so it will need access_key_id and secret_access_key
