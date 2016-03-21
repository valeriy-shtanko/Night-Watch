Night-Watch
--------------------------------------
Environment test scenario processing and verification.

Scenario structure
--------------------------------------

#                                     - comment
-> 1234, 2345, 123, 345               - skipped messages id
*> key1=value1                        - key/value pairs which will be added to defines
*> key2=value2
     ...
{                                     - scenario item start
$> 345                                - expected message ID (not checked if less than zero value defined)
<= {"key1":"value1","key3":"value3"}  - send json data (can contains ${name} place holders)
=> <json-path-1>=value-1              - verified json values in received json data
=> <json-path-2>=value-2
     ...
+> key-1=<json-path-1>                - add to scenario defines key/value pairs from received data
+> key-2=<json-path-2>
     ...
?> <json-path-1> = value-1            - skip message conditions, message will be skipped if one of them is true
?> <json-path-2> = value-2
     ...
}                                     - scenario item end
     ...
