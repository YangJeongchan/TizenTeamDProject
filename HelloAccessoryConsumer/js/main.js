/*    
 * Copyright (c) 2014 Samsung Electronics Co., Ltd.   
 * All rights reserved.   
 *   
 * Redistribution and use in source and binary forms, with or without   
 * modification, are permitted provided that the following conditions are   
 * met:   
 *   
 *     * Redistributions of source code must retain the above copyright   
 *        notice, this list of conditions and the following disclaimer.  
 *     * Redistributions in binary form must reproduce the above  
 *       copyright notice, this list of conditions and the following disclaimer  
 *       in the documentation and/or other materials provided with the  
 *       distribution.  
 *     * Neither the name of Samsung Electronics Co., Ltd. nor the names of its  
 *       contributors may be used to endorse or promote products derived from  
 *       this software without specific prior written permission.  
 *  
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS  
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT  
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR  
 * A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT  
 * OWNER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,  
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT  
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,  
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY  
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT  
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE  
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
 
var SAAgent = null;
var SASocket = null;
var CHANNELID = 104;
var ProviderAppName = "HelloAccessoryProvider";
var pe = null;
var control = null;
var stepCount = null;
//<!-- Sample Code by Victor Dibia, Dencycom.com May 16, 2014 -->




function createHTML(log_string)
{
	var log = document.getElementById('resultBoard');
	log.innerHTML = log.innerHTML + "<br> : " + log_string;
}

function onerror(err) {
	console.log("err [" + err + "]");
}

var agentCallback = {
	onconnect : function(socket) {
		SASocket = socket;
		alert("HelloAccessory Connection established with RemotePeer");
		createHTML("startConnection");
		SASocket.setSocketStatusListener(function(reason){
			console.log("Service connection lost, Reason : [" + reason + "]");
			disconnect();
		});
		
		fetch();
		
	},
	onerror : onerror
};

var peerAgentFindCallback = {
	onpeeragentfound : function(peerAgent) {
		try {
			if (peerAgent.appName == ProviderAppName) {
				SAAgent.setServiceConnectionListener(agentCallback);
				SAAgent.requestServiceConnection(peerAgent);
			} else {
				alert("Not expected app!! : " + peerAgent.appName);
			}
		} catch(err) {
			console.log("exception [" + err.name + "] msg[" + err.message + "]");
		}
	},
	onerror : onerror
}

function onsuccess(agents) {
	try {
		if (agents.length > 0) {
			SAAgent = agents[0];
			
			SAAgent.setPeerAgentFindListener(peerAgentFindCallback);
			SAAgent.findPeerAgents();
		} else {
			alert("Not found SAAgent!!");
		}
	} catch(err) {
		console.log("exception [" + err.name + "] msg[" + err.message + "]");
	}
}

function connect() {
	if (SASocket) {
		alert('Already connected!');
        return false;
    }
	
	try {
		webapis.sa.requestSAAgent(onsuccess, function (err) {
			console.log("err [" + err.name + "] msg[" + err.message + "]");
		});
	} catch(err) {
		console.log("exception [" + err.name + "] msg[" + err.message + "]");
	}
}

function disconnect() {
	control.stop();
//	try {
//		if (SASocket != null) {
//			SASocket.close();
//			SASocket = null;
//			createHTML("closeConnection");
//		}
//	} catch(err) {
//		console.log("exception [" + err.name + "] msg[" + err.message + "]");
//	}
	
}

function onreceive(channelId, data) {
	createHTML(data);
}

function fetch() {
	try {
		SASocket.setDataReceiveListener(onreceive);
		//var currentCalorie = pedometerData.calorie;
		//var currentSteps = pedometerData.totalStep;
		var sendMsg = 'calorie: '+pe;
		
		SASocket.sendData(CHANNELID, sendMsg);
	} catch(err) {
		console.log("exception [" + err.name + "] msg[" + err.message + "]");
	}
}
/**
 * Initializes the module
 */
window.onload = function () {
    // add eventListener for tizenhwkey
	 console.log("start");
    document.addEventListener('tizenhwkey', function(e) {
        if(e.keyName == "back")
            tizen.application.getCurrentApplication().exit();
    });    
    
    control = (function (){
    	//alert('pedo here');

    	var pedometer = null,
        pedometerData = {},
        CONTEXT_TYPE = 'PEDOMETER';

    	if (window.webapis && window.webapis.motion !== undefined) {
            pedometer = window.webapis.motion;
            console.log('Pedometer found');
           
            start();
        }else {
        	console.log('Pedometer not found');
        }

    	 //Start pedomenter ;
    	 function getPedometerData(pedometerInfo) {
             var pData = {
                 calorie: pedometerInfo.cumulativeCalorie,
                 distance: pedometerInfo.cumulativeDistance,
                 runDownStep: pedometerInfo.cumulativeRunDownStepCount,
                 runStep: pedometerInfo.cumulativeRunStepCount,
                 runUpStep: pedometerInfo.cumulativeRunUpStepCount,
                 speed: pedometerInfo.speed,
                 stepStatus: pedometerInfo.stepStatus,
                 totalStep: pedometerInfo.cumulativeTotalStepCount,
                 walkDownStep: pedometerInfo.cumulativeWalkDownStepCount,
                 walkStep: pedometerInfo.cumulativeWalkStepCount,
                 walkUpStep: pedometerInfo.cumulativeWalkUpStepCount,
                 walkingFrequency: pedometerInfo.walkingFrequency
             };

             pedometerData = pData;
             return pData;
         }

        
         function getData() {
             return pedometerData;
         }

         function resetData() {
             pedometerData = {
                 calorie: 0,
                 distance: 0,
                 runDownStep: 0,
                 runStep: 0,
                 runUpStep: 0,
                 speed: 0,
                 stepStatus: '',
                 totalStep: 0,
                 walkDownStep: 0,
                 walkStep: 0,
                 walkUpStep: 0,
                 walkingFrequency: 0
             };
         }

         function handlePedometerInfo(pedometerInfo, eventName) {
        	 pedometerData = getPedometerData(pedometerInfo)
        	 console.log('Total Steps : ' + pedometerData.totalStep);
        	 pe= pedometerData.calorie;
        	 stepCount = pedometerData.totalStep;
        	 document.getElementById("cal").innerHTML = "Calorie : "+pe;
        	 document.getElementById("step").innerHTML = "Step : "+stepCount;
        	 //document.getElementById("calories").innerHTML =  'Total Steps : ' + pedometerData.totalStep;
        	// document.getElementById("steps").innerHTML = 'Calories Burnt : ' + pedometerData.calorie;
        	

         }

        
         function start() {
             resetData();
          
             pedometer.start(
                 CONTEXT_TYPE,
                 function onSuccess(pedometerInfo) {
                	 
                     handlePedometerInfo(pedometerInfo, 'pedometer.change');
                 }
             );
         }

         function stop() {
        	 pedometer.stop(CONTEXT_TYPE);
        	 pedometerData = getPedometerData(pedometerInfo);
        	 pe = pedometerData.calorie;
        	 document.getElementById("cal").innerHTML = "Calorie : " + pe;
         }

    });
    control();
};
