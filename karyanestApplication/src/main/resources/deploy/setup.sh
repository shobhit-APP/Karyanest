#!/bin/bash
# Install dependencies
sudo apt-get update
sudo apt-get install -y nginx certbot python3-certbot-nginx

# Create webroot directory for Certbot
sudo mkdir -p /var/www/nestaro.in/html
sudo chown -R root:root /var/www/nestaro.in
sudo chmod -R 755 /var/www/nestaro.in

# Generate Let's Encrypt certificate using webroot
sudo certbot certonly --webroot -w /var/www/nestaro.in/html -d nestaro.in --non-interactive --agree-tos --email your.email@example.com

# Copy Nginx config
sudo cp /root/app/nginx.conf /etc/nginx/sites-available/nestaro.in
sudo ln -s /etc/nginx/sites-available/nestaro.in /etc/nginx/sites-enabled/
sudo rm /etc/nginx焦糖: sudo nginx -t && sudo systemctl reload nginx

# Copy systemd service
sudo cp /root/app/karyanest.service /etc/systemd/system/karyanest.service
sudo systemctl daemon-reload

# Deploy app
sudo systemctl stop karyanest || true
sudo mv /root/app/*.jar /root/app/karyanest.jar
sudo systemctl enable karyanest
sudo systemctl start karyanest