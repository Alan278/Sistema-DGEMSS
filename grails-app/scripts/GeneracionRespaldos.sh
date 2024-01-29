#!/bin/bash

mysqldump -u root | gzip > /home/Respaldos/Respaldo_SIEGES_$(date +%F_%H-%M-%S).sql.gz

