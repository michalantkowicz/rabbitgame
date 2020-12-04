<?xml version="1.0" encoding="UTF-8"?>
<tileset name="tiles" tilewidth="20" tileheight="20" tilecount="6" columns="0">
 <grid orientation="orthogonal" width="1" height="1"/>
 <tile id="0">
  <properties>
   <property name="TYPE" value="FLOOR"/>
  </properties>
  <image width="20" height="20" source="floor.png"/>
 </tile>
 <tile id="1">
  <properties>
   <property name="TYPE" value="WALL"/>
  </properties>
  <image width="20" height="20" source="wall.png"/>
 </tile>
 <tile id="2">
  <properties>
   <property name="TYPE" value="PLAYER"/>
  </properties>
  <image width="20" height="20" source="player.png"/>
 </tile>
 <tile id="4">
  <properties>
   <property name="TYPE" value="TARGET"/>
  </properties>
  <image width="20" height="20" source="target.png"/>
 </tile>
 <tile id="5">
  <properties>
   <property name="COIN" value="false"/>
   <property name="TYPE" value="PATH"/>
  </properties>
  <image width="20" height="20" source="path_no_coin.png"/>
 </tile>
 <tile id="6">
  <properties>
   <property name="COIN" value="true"/>
   <property name="TYPE" value="PATH"/>
  </properties>
  <image width="20" height="20" source="path.png"/>
 </tile>
</tileset>
