# shellcheck disable=SC2091
SERVER="$(cat local.properties | grep "server" | head -n1 | sed -e 's/server=//g')"
SERVER_PATH="$(cat local.properties | grep "server_path" | head -n1 | sed -e 's/server_path=//g')"

BACKEND_SERVICE_NAME="mtuci-rasp-backend"
PAESER_SERVICE_NAME="mtuci-rasp-parser"
ICS_SERVICE_NAME="mtuci-rasp-ics_backend"

echo "Building projects"
./gradlew -p parser jar buildService
./gradlew -p ics_backend jar buildService
./gradlew -p backend bootJar buildService

echo "Stopping service"
ssh $SERVER "sudo systemctl stop $BACKEND_SERVICE_NAME"
ssh $SERVER "sudo systemctl disable $BACKEND_SERVICE_NAME"
ssh $SERVER "sudo systemctl stop $PAESER_SERVICE_NAME"
ssh $SERVER "sudo systemctl disable $PAESER_SERVICE_NAME"
ssh $SERVER "sudo systemctl stop $ICS_SERVICE_NAME"
ssh $SERVER "sudo systemctl disable $ICS_SERVICE_NAME"

echo "Copy artifacts"
ssh $SERVER "mkdir \"$SERVER_PATH/jar\""
scp ./parser/build/libs/parser.jar "$SERVER:$SERVER_PATH/jar"
scp ./backend/build/libs/backend.jar "$SERVER:$SERVER_PATH/jar"
scp ./ics_backend/build/libs/ics_backend.jar "$SERVER:$SERVER_PATH/jar"

scp ./backend/build/$BACKEND_SERVICE_NAME.service "$SERVER:$SERVER_PATH"
scp ./parser/build/$PAESER_SERVICE_NAME.service "$SERVER:$SERVER_PATH"
scp ./ics_backend/build/$ICS_SERVICE_NAME.service "$SERVER:$SERVER_PATH"

ssh $SERVER "sudo rm /etc/systemd/system/$BACKEND_SERVICE_NAME.service"
ssh $SERVER "sudo rm /etc/systemd/system/$PAESER_SERVICE_NAME.service"
ssh $SERVER "sudo rm /etc/systemd/system/$ICS_SERVICE_NAME.service"
ssh $SERVER "sudo mv $SERVER_PATH/$BACKEND_SERVICE_NAME.service /etc/systemd/system/$BACKEND_SERVICE_NAME.service"
ssh $SERVER "sudo mv $SERVER_PATH/$PAESER_SERVICE_NAME.service /etc/systemd/system/$PAESER_SERVICE_NAME.service"
ssh $SERVER "sudo mv $SERVER_PATH/$ICS_SERVICE_NAME.service /etc/systemd/system/$ICS_SERVICE_NAME.service"

echo "Starting services"
ssh $SERVER "sudo systemctl enable $BACKEND_SERVICE_NAME"
ssh $SERVER "sudo systemctl start $BACKEND_SERVICE_NAME"
ssh $SERVER "sudo systemctl enable $PAESER_SERVICE_NAME"
ssh $SERVER "sudo systemctl start $PAESER_SERVICE_NAME"
ssh $SERVER "sudo systemctl enable $ICS_SERVICE_NAME"
ssh $SERVER "sudo systemctl start $ICS_SERVICE_NAME"
