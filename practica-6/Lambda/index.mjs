import { DynamoDBClient } from "@aws-sdk/client-dynamodb";
import { DynamoDBDocumentClient, PutCommand, ScanCommand, QueryCommand } from "@aws-sdk/lib-dynamodb";

const client = new DynamoDBClient({});
const docClient = DynamoDBDocumentClient.from(client);
const TABLE_NAME = "ReservasLaboratorio";

export const handler = async (event) => {
    const method = event.requestContext?.http?.method || event.httpMethod;
    const path = event.requestContext?.http?.path || event.path;
    const now = new Date().toISOString();

    try {
        // --- 1. REGISTRAR RESERVA (POST) ---
        if (method === 'POST') {
            const { correo, nombre, id_estudiante, horario, laboratorio } = JSON.parse(event.body);
            const fecha = new Date(horario);
            const hora = fecha.getHours();

            // Regla: Horario de 8 AM a 10 PM y múltiplos de hora [cite: 25]
            if (hora < 8 || hora >= 22 || fecha.getMinutes() !== 0) {
                return response(400, { message: "Error: Solo horas exactas entre 8 AM y 10 PM." });
            }

            // Regla: Máximo 7 personas por hora [cite: 24]
            const checkCapacidad = await docClient.send(new QueryCommand({
                TableName: TABLE_NAME,
                KeyConditionExpression: "laboratorio = :l AND fecha_hora = :f",
                ExpressionAttributeValues: { ":l": laboratorio, ":f": horario }
            }));

            if (checkCapacidad.Items.length >= 7) {
                return response(400, { message: "Capacidad máxima de 7 personas alcanzada." });
            }

            // Persistencia de los datos [cite: 23]
            await docClient.send(new PutCommand({
                TableName: TABLE_NAME,
                Item: { laboratorio, fecha_hora: horario, correo, nombre, id_estudiante, activa: true }
            }));
            return response(201, { message: "Reserva exitosa" });
        }

        // --- 2. LISTAR RESERVAS ACTIVAS (GET) --- 
        if (path.includes('activas')) {
            const data = await docClient.send(new ScanCommand({
                TableName: TABLE_NAME,
                FilterExpression: "fecha_hora >= :n",
                ExpressionAttributeValues: { ":n": now }
            }));
            return response(200, data.Items);
        }

        // --- 3. REGISTROS PASADOS (GET) --- 
        if (path.includes('pasadas')) {
            const { desde, hasta } = event.queryStringParameters || {};
            const data = await docClient.send(new ScanCommand({
                TableName: TABLE_NAME,
                FilterExpression: "fecha_hora BETWEEN :d AND :h",
                ExpressionAttributeValues: { ":d": desde, ":h": hasta }
            }));
            return response(200, data.Items);
        }
    } catch (e) {
        return response(500, { error: e.message });
    }
};

const response = (status, data) => ({
    statusCode: status,
    headers: { "Access-Control-Allow-Origin": "*", "Content-Type": "application/json" },
    body: JSON.stringify(data)
});