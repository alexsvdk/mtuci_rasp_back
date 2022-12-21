# shellcheck disable=SC2091
SERVER="$(cat local.properties | grep "server" | head -n1 | sed -e 's/server=//g')"
SERVER_PATH="$(cat local.properties | grep "server_path" | head -n1 | sed -e 's/server_path=//g')"

BACKEND_SERVICE_NAME="mtuci-rasp-backend"
PAESER_SERVICE_NAME="mtuci-rasp-parser"

echo "Building projects"
./gradlew -p parser jar buildService
./gradlew -p ics_backend buildFatJar buildService
./gradlew -p backend bootJar buildService

echo "Stopping service"
ssh $SERVER "sudo systemctl stop $BACKEND_SERVICE_NAME"
ssh $SERVER "sudo systemctl disable $BACKEND_SERVICE_NAME"
ssh $SERVER "sudo systemctl stop $PAESER_SERVICE_NAME"
ssh $SERVER "sudo systemctl disable $PAESER_SERVICE_NAME"

echo "Copy artifacts"
ssh $SERVER "mkdir \"$SERVER_PATH/jar\""
scp ./parser/build/libs/parser.jar "$SERVER:$SERVER_PATH/jar"
scp ./backend/build/libs/backend.jar "$SERVER:$SERVER_PATH/jar"
scp ./backend/build/mtuci-rasp-backend.service "$SERVER:$SERVER_PATH"
scp ./parser/build/mtuci-rasp-parser.service "$SERVER:$SERVER_PATH"
ssh $SERVER "sudo rm /etc/systemd/system/mtuci-rasp-backend.service"
ssh $SERVER "sudo rm /etc/systemd/system/mtuci-rasp-parser.service"
ssh $SERVER "sudo mv $SERVER_PATH/mtuci-rasp-backend.service /etc/systemd/system/mtuci-rasp-backend.service"
ssh $SERVER "sudo mv $SERVER_PATH/mtuci-rasp-parser.service /etc/systemd/system/mtuci-rasp-parser.service"

echo "Starting services"
ssh $SERVER "sudo systemctl enable $BACKEND_SERVICE_NAME"
ssh $SERVER "sudo systemctl start $BACKEND_SERVICE_NAME"
ssh $SERVER "sudo systemctl enable $PAESER_SERVICE_NAME"
ssh $SERVER "sudo systemctl start $PAESER_SERVICE_NAME"
