import {mkdir,rm,copyFile,stat} from 'node:fs/promises';
const output=new URL('../site-dist/',import.meta.url),source=new URL('../site/',import.meta.url);
const jar=new URL('../target/campus-connect-1.0.0.jar',import.meta.url);await stat(jar);
await rm(output,{recursive:true,force:true});await mkdir(new URL('assets/',output),{recursive:true});
for(const file of ['index.html','style.css','app.js','assets/overview.png','assets/planner.png','assets/explore.png'])await copyFile(new URL(file,source),new URL(file,output));
await copyFile(jar,new URL('campus-connect.jar',output));console.log('Packaged 7 allowlisted files. No local data or credentials copied.');
