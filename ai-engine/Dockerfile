# Stage 1: Build Dependencies
FROM python:3.12-slim AS builder
WORKDIR /app
COPY requirements.txt .
RUN pip install --no-cache-dir --user -r requirements.txt

# Stage 2: Runtime Image
FROM python:3.12-slim
RUN groupadd -r sentinel && useradd -r -g sentinel sentinel
WORKDIR /app
COPY --from=builder /root/.local /home/sentinel/.local
COPY anomaly-detection/ .
RUN chown -R sentinel:sentinel /app
USER sentinel
ENV PATH=/home/sentinel/.local/bin:$PATH
EXPOSE 8000
HEALTHCHECK --interval=15s --timeout=5s --retries=5 CMD python -c "import urllib.request; urllib.request.urlopen('http://localhost:8000/api/v1/ai/status')" || exit 1
CMD ["python", "-m", "app.main"]
