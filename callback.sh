#! /bin/bash

password="swarch"

serverLog="logs/server.log"
clientLog="logs/client.log"

case $1 in
    "ping") 
        hosts=${@:2}
        for h in $hosts; 
        do
            echo "pinging $h..."
            ping -n 3 $h
            echo -e "\n"
        done ;;

    "send")
        hosts=${@:2}
        callbackDir="talker-ciclo-kbd-AlexanderSanchez-DavidMontano"

        cd ..
        currentDir=$(pwd)  
        echo -e "current dir: $currentDir \n"
        zip -r callback.zip $callbackDir > /dev/null 2>&1
        for h in $hosts;
        do 
            echo -e "creating directory AlexanderSanchez-DavidMontano in $h... \n"
            sshpass -p $password ssh -o StrictHostKeyChecking=no swarch@$h "mkdir AlexanderSanchez-DavidMontano/"
            echo -e "sending callback.zip to $h... \n"
            sshpass -p $password scp callback.zip swarch@$h:AlexanderSanchez-DavidMontano/ 
            echo -e "unzipping callback.zip in $h... \n"
            sshpass -p $password ssh swarch@$h 'cd AlexanderSanchez-DavidMontano; unzip -o callback.zip '
            echo -e "removing callback.zip in $h... \n"
            sshpass -p $password ssh swarch@$h 'cd AlexanderSanchez-DavidMontano; rm callback.zip'
        done ;;

    "build")
        server=$2
        hosts=${@:3}
        echo "building in server $server..."
        sshpass -p $password ssh -o StrictHostKeyChecking=no swarch@$server "cd AlexanderSanchez-DavidMontano/talker-ciclo-kbd-AlexanderSanchez-DavidMontano; sed -i 's/Ice.Default.Host=localhost/Ice.Default.Host=$server/' server/src/main/resources/config.server; gradle :server:build"
        echo "Build completed"

        for h in $hosts;
        do
            oldClientEndpoint="Callback.Client.Endpoints=default -h localhost"
            newClientEndpoint="Callback.Client.Endpoints=default -h $h"
            echo -e "building in client $h... \n"
            sshpass -p $password ssh -o StrictHostKeyChecking=no swarch@$h "cd AlexanderSanchez-DavidMontano/talker-ciclo-kbd-AlexanderSanchez-DavidMontano; sed -i \"s/$oldClientEndpoint/$newClientEndpoint/\" client/src/main/resources/config.client; sed -i \"s/Ice.Default.Host=localhost/Ice.Default.Host=$server/\" client/src/main/resources/config.client; gradle :client:build"
            echo -e "\n"
        done ;;

    "run")
        server=$2
        number=$3
        hosts=${@:4}
        date=$(date +"%Y-%m-%d %H:%M:%S")
        echo -e "running server in $server... \n"
        echo "Execution Log on $date" >> $serverLog
        echo "Execution Log on $date" >> $clientLog
        sshpass -p $password ssh -o StrictHostKeyChecking=no swarch@$server 'cd AlexanderSanchez-DavidMontano/talker-ciclo-kbd-AlexanderSanchez-DavidMontano; java -jar server/build/libs/server.jar' &>> $serverLog &
        sleep 5
        pids=()
        for h in $hosts;
        do
            echo -e "running client in $h with input number $number... \n"
            sshpass -p $password ssh -o StrictHostKeyChecking=no swarch@$h "cd AlexanderSanchez-DavidMontano/talker-ciclo-kbd-AlexanderSanchez-DavidMontano; java -jar client/build/libs/client.jar $number" &>> $clientLog &
            pids+=($!)
            # sshpass -p $password ssh swarch@$h ' pkill -f client.jar; exit'
        done 
        
        for pid in ${pids[@]}; do
            wait $pid
        done

        sshpass -p $password ssh swarch@$server ' pkill -f server.jar; exit'
        echo -e "execution on server finished \n" ;;
esac