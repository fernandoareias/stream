# Use the official Python slim image
FROM python:3.8-slim

# Set environment variables
ENV PYTHONDONTWRITEBYTECODE 1
ENV PYTHONUNBUFFERED 1

# Set the working directory
WORKDIR /app

# Copy the requirements file
COPY requirements.txt /app/

# Install necessary packages
RUN apt-get update && apt-get install -y \
    libpq-dev && \
    pip install --upgrade pip && \
    pip install -r requirements.txt

# Copy the rest of the application
COPY . /app/

# Expose the port PostgreSQL is using (5432)
EXPOSE 5432

# Command to run the script
CMD ["python3", "proposals.py"]
